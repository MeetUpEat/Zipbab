package com.bestapp.zipbab.ui.meetupmap

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentMeetUpMapBinding
import com.bestapp.zipbab.permission.LocationPermissionManager
import com.bestapp.zipbab.permission.LocationPermissionSnackBar
import com.bestapp.zipbab.userlocation.hasLocationPermission
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeetUpMapFragment : Fragment() {
    private val viewModel: MeetUpMapViewModel by viewModels()

    private var _binding: FragmentMeetUpMapBinding? = null
    private val binding: FragmentMeetUpMapBinding get() = _binding!!

    private val locationPermissionSnackBar = LocationPermissionSnackBar(this)
    private val locationPermissionManager = LocationPermissionManager(
        fragment = this,
        locationPermissionSnackBar = locationPermissionSnackBar
    )

    private val meetUpListAdapter = MeetUpListAdapter { position ->
        selectedMeeingItem(position)
    }

    private var _naverMap: NaverMap? = null
    private val naverMap get() = _naverMap!!

    private lateinit var locationSource: FusedLocationSource
    private lateinit var cameraPosition: CameraPosition

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetUpMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        initObserve()
        initMapView()
        initBottomSheet()
    }

    override fun onResume() {
        super.onResume()

        if (!requireContext().hasLocationPermission()) {
            locationPermissionSnackBar.showPermissionSettingSnackBar()
        } else {
            viewModel.setRequestPermissionResult(true)
        }
    }

    private fun checkPermission() {
        val isGranted = requireContext().hasLocationPermission()
        viewModel.setRequestPermissionResult(isGranted)
    }

    private fun initObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLocationPermissionGranted.collect { isGranted ->
                if (isGranted) {
                    locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE )

                    if (_naverMap != null) {
                        naverMap.locationSource = locationSource
                        naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    }
                } else {
                    locationPermissionManager.requestPermission()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetUpMapUiState.collectLatest {
                if (it.meetUpMapMeetingUis.isNotEmpty()) {
                    Log.d("Test", "addMeetingMarkers")
                    val meetingLabels = naverMap.addMeetingMarkers(requireContext(), it)
                    // viewModel.setMeetingLabels(meetingLabels)
                }
            }
        }
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fl_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fl_map_view, it).commit()
            }

        Log.d("Test", "getMapAsync1")
        mapFragment.getMapAsync { map ->
            _naverMap = map
            Log.d("Test", "getMapAsync2")

            if (::locationSource.isInitialized) {
                naverMap.locationSource = locationSource
                // 위치를 추적하면서 카메라도 따라 움직인다. map 드래그 시 Follow 모드 해제됨
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                Log.d("Test", "Follow 모드 활성화")
            }

            naverMap.uiSettings.isLocationButtonEnabled = true // GPS 버튼 활성화
            naverMap.uiSettings.isTiltGesturesEnabled = false // 틸트(like 모니터) 비활성화
            naverMap.isIndoorEnabled = true // 실내 지도 활성화(선택)

            naverMap.locationOverlay.circleRadius = 120 // 반투명 원(위치 정확도 UX) 크기

            // 카메라 중심 셋업을 위해 바텀 시트 높이만큼 패딩 주기
            // 지도 영역의 변화는 없음, 네이버 로고 및 GPS 아이콘에도 적용됨

            val maxHeight = (resources.displayMetrics.heightPixels * MAX_HEIGHT).toInt()
            val bottomPaddingValue = (maxHeight * PADDING_BOTTOM).toInt()

            naverMap.setContentPadding(0, 0, 0, bottomPaddingValue)

            // 맵 드래그 or 클릭 시 바텀시트 최소화
            naverMap.setOnMapClickListener { pointF: PointF, latLng: LatLng ->
                Log.d("Map", "맵 클릭")
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            naverMap.addOnLocationChangeListener { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                viewModel.getMeetings(latLng)
            }
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // 바텀시트가 숨겨지지 않도록 지정함
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        /**
         *  slideOffset : -1.0 ~ 1.0 범위
         *  -1 : 완전히 숨겨짐 - STATE_HIDDEN
         *   0 : 중간쯤 펼쳐짐 - STATE_HALF_EXPANDED
         *   1 : 완전히 펼쳐짐 - STATE_EXPANDED
         */
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    /** Behavior 상태                               slideOffset
     *  STATE_EXPANDED : 완전히 펼쳐진 상태              1.0
     *  STATE_HALF_EXPANDED : 절반으로 펼쳐진 상태       0.5
     *  STATE_COLLAPSED : 접혀있는 상태                   0
     *  STATE_HIDDEN : 아래로 숨겨진 상태 (보이지 않음)    -1
     *  STATE_DRAGGING : 드래깅되고 있는 상태
     *  STATE_SETTLING : 드래그/스와이프 직후 고정된 상태
     */
    private fun initBottomSheet() {
        viewModel.getUserNickname()

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.layout.bsMeetings)
        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        // 초기 세팅: 절반만 확장된 상태
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        // 절반 확장(STATE_HALF_EXPANDED) 시, 최대 높이 비율 지정(0 ~ 1.0)
        standardBottomSheetBehavior.halfExpandedRatio = HALF_EXPANDED_RAUIO
        // 접혀있는 상태(STATE_COLLAPSED)일 때의 고정 높이 지정
        standardBottomSheetBehavior.setPeekHeight(PEEK_HEIGHT, true)

        binding.root.doOnLayout {
            val maxHeight = (resources.displayMetrics.heightPixels * MAX_HEIGHT).toInt()
            standardBottomSheetBehavior.maxHeight = maxHeight
        }

        binding.layout.rv.adapter = meetUpListAdapter
        binding.layout.rv.layoutManager = LinearLayoutManager(requireContext())

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.layout.rv.addItemDecoration(dividerItemDecoration)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetUpMapUiState.collectLatest {
                if (it.meetUpMapMeetingUis.isNotEmpty()) {
                    meetUpListAdapter.submitList(it.meetUpMapMeetingUis)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userNickname.collect() {
                binding.layout.tvUserNickname.text =
                    getString(R.string.meet_up_map_nickname).format(it)
            }
        }
    }

    private fun selectedMeeingItem(position: Int) {
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

//        if (meetingLabels.size > position) {
//            map.moveToCamera(meetingLabels[position].position)
//        }
    }

    override fun onDestroyView() {
//        viewModel.removeUserLabel()
        binding.layout.rv.adapter = null
//        binding.mv.finish()
        _binding = null
//        _map = null
        standardBottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)

        super.onDestroyView()
    }

    companion object {
        const val HALF_EXPANDED_RAUIO = 0.3f
        const val PEEK_HEIGHT = 300
        const val MAX_HEIGHT = 0.7f

        const val LOCATION_PERMISSION_REQUEST_CODE = 1_000
        const val PADDING_BOTTOM = 0.4f
    }
}