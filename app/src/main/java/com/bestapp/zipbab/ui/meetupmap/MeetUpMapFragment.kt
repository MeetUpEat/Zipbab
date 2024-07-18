package com.bestapp.zipbab.ui.meetupmap

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentMeetUpMapBinding
import com.bestapp.zipbab.permission.LocationPermissionManager
import com.bestapp.zipbab.permission.LocationPermissionSnackBar
import com.bestapp.zipbab.userlocation.hasLocationPermission
import com.bestapp.zipbab.util.safeNavigate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
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
    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var meetingMarkers: List<Marker>
    private var lastUserLocation: LatLng? = null
    private var nightModeEnabled = true

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
        initBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserUiState()

        if (!requireContext().hasLocationPermission()) {
            locationPermissionSnackBar.showPermissionSettingSnackBar()
        } else {
            locationPermissionSnackBar.hidePermissionSettingSnackBar()
            viewModel.setRequestPermissionResult(true)
            initMapView()
        }
    }

    private fun checkPermission() {
        val isGranted = requireContext().hasLocationPermission()
        viewModel.setRequestPermissionResult(isGranted)
    }

    private fun initObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLocationPermissionGranted.collect { isGranted ->
                    if (isGranted) {
                        locationSource =
                            FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)

                        if (_naverMap != null) {
                            naverMap.locationSource = locationSource
                            naverMap.locationTrackingMode = LocationTrackingMode.Follow
                        }
                    } else {
                        locationPermissionManager.requestPermission()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meetUpMapUiState.collect() {
                    if (it.meetUpMapMeetingUis.isEmpty() || _naverMap == null) {
                        return@collect
                    }

                    meetingMarkers = addMeetingMarkers(
                        requireContext(),
                        it
                    ) { meetingDocumentID, isHost ->
                        val action =
                            MeetUpMapFragmentDirections.actionMeetUpMapFragmentToMeetingInfoFragment(
                                meetingDocumentID
                            )
                        action.safeNavigate(this@MeetUpMapFragment)
                    }

                    meetingMarkers.map {
                        val isDarkMode = isSystemInDarkMode()
                        it.switchNightMode(isDarkMode)
                    }

                    viewModel.setMeetingLabels(meetingMarkers)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetingMarkerUiStates.collect {
                if (!it.isMapReady || it.meetingMarkers.isEmpty()) {
                    return@collect
                }

                it.meetingMarkers.forEach { marker ->
                    marker.map = naverMap
                }
            }
        }
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fl_map_view) as? MapFragment
            ?: MapFragment.newInstance(
                NaverMapOptions()
                    .nightModeEnabled(true) // 다크 모드 지원여부
                    .backgroundColor(NaverMap.DEFAULT_BACKGROUND_COLOR_DARK)
                    .backgroundResource(NaverMap.DEFAULT_BACKGROUND_DRWABLE_DARK)
                    .mapType(NaverMap.MapType.Navi) // 다크 모드 사용 시 Navi MapType에서만 가능함
            ).also {
                fm.beginTransaction()
                    .replace(R.id.fl_map_view, it)
                    .commit()
            }

        mapFragment.getMapAsync { map ->
            _naverMap = map
            viewModel.setMapReady(true)

            if (::locationSource.isInitialized) {
                naverMap.locationSource = locationSource
                // 위치를 추적하면서 카메라도 따라 움직인다. map 드래그 시 Follow 모드 해제됨
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            }

            naverMap.uiSettings.isLocationButtonEnabled = true // GPS 버튼 활성화
            naverMap.uiSettings.isTiltGesturesEnabled = false // 틸트(like 모니터) 비활성화

            // 반투명 원(위치 정확도 UX) 크기 ZoomLevel에 따라 유동적이지 않음
            naverMap.locationOverlay.circleRadius = LocationOverlay.SIZE_AUTO
            naverMap.locationOverlay.iconHeight = LocationOverlay.SIZE_AUTO

            // 카메라 중심 셋업을 위해 바텀 시트 높이만큼 패딩 주기
            // 지도 영역의 변화는 없음, 네이버 로고 및 GPS 아이콘에도 적용됨
            val maxHeight = (resources.displayMetrics.heightPixels * MAX_HEIGHT).toInt()
            val bottomPaddingValue = (maxHeight * PADDING_BOTTOM).toInt()

            naverMap.setContentPadding(0, 0, 0, bottomPaddingValue)

            val isDarkMode = isSystemInDarkMode()
            val meetingMarkerList = if (::meetingMarkers.isInitialized) {
                meetingMarkers
            } else {
                emptyList()
            }

            naverMap.switchNightMode(isDarkMode, meetingMarkerList)

            initMapListener()
        }
    }

    private fun initMapListener() {
        naverMap.addOnLocationChangeListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)

            if (lastUserLocation == null) {
                lastUserLocation = latLng
                viewModel.getMeetings(latLng)
            }

            if (getDiffDistance(latLng) >= THRESHOLD_DISTANCE_FOR_UPDATE) {
                lastUserLocation = latLng
                viewModel.getMeetings(latLng)
                binding.layout.rv.scrollToPosition(0)
            }
        }

        naverMap.addOnOptionChangeListener {
            Log.d("test1", "addOnOptionChangeListener")
            if (nightModeEnabled == naverMap.isNightModeEnabled) {
                return@addOnOptionChangeListener
            }
            nightModeEnabled = naverMap.isNightModeEnabled
            naverMap.backgroundColor =
                if (nightModeEnabled) NaverMap.DEFAULT_BACKGROUND_COLOR_DARK else NaverMap.DEFAULT_BACKGROUND_COLOR_LIGHT
            naverMap.setBackgroundResource(if (nightModeEnabled) NaverMap.DEFAULT_BACKGROUND_DRWABLE_DARK else NaverMap.DEFAULT_BACKGROUND_DRWABLE_LIGHT)

            val isDarkMode = isSystemInDarkMode()

            if (::meetingMarkers.isInitialized) {
                naverMap.switchNightMode(isDarkMode, meetingMarkers)
            }
        }

        naverMap.addOnCameraChangeListener { reason, _ ->
            // 사용자의 제스쳐로 인해 Camera가 변경된 경우, 바텀시트 축소
            if (reason == CameraUpdate.REASON_GESTURE) {
                if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        }
    }

    private fun isSystemInDarkMode() = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    private fun getDiffDistance(latLng: LatLng) = haversine(lastUserLocation as LatLng, latLng)

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun initBottomSheet() {
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.layout.bsMeetings)
        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        // 초기 세팅: 절반만 확장된 상태
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        // 절반 확장(STATE_HALF_EXPANDED) 시, 최대 높이 비율 지정(0 ~ 1.0)
        standardBottomSheetBehavior.halfExpandedRatio = HALF_EXPANDED_RAUIO
        // 접혀있는 상태(STATE_COLLAPSED)일 때의 고정 높이 지정
        standardBottomSheetBehavior.setPeekHeight(PEEK_HEIGHT, true)

        binding.layout.rv.adapter = meetUpListAdapter
        binding.layout.rv.layoutManager = LinearLayoutManager(requireContext())

        setBottomSheetMaxHeight()

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.layout.rv.addItemDecoration(dividerItemDecoration)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meetUpMapUiState.collectLatest {
                    if (it.meetUpMapMeetingUis.isNotEmpty()) {
                        meetUpListAdapter.submitList(it.meetUpMapMeetingUis)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userUiState.collect() {
                    binding.layout.tvUserNickname.text =
                        getString(R.string.meet_up_map_nickname).format(it.nickname)
                }
            }
        }
    }

    private fun setBottomSheetMaxHeight() {
        val maxHeight = (resources.displayMetrics.heightPixels * MAX_HEIGHT).toInt()
        standardBottomSheetBehavior.maxHeight = maxHeight
    }

    private fun selectedMeeingItem(position: Int) {
        if (::meetingMarkers.isInitialized.not()) {
            return
        }

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        if (meetingMarkers.size > position) {
            naverMap.moveToPosition(meetingMarkers[position].position)
        }
    }

    override fun onDestroyView() {
        binding.layout.rv.adapter = null
        _binding = null
        standardBottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
        viewModel.setMapReady(false)
        _naverMap = null

        if (::meetingMarkers.isInitialized) {
            meetingMarkers.forEach {
                it.map = null
            }
        }

        super.onDestroyView()
    }

    companion object {
        const val HALF_EXPANDED_RAUIO = 0.3f
        const val PEEK_HEIGHT = 300
        const val MAX_HEIGHT = 0.65f

        const val LOCATION_PERMISSION_REQUEST_CODE = 1_000
        const val PADDING_BOTTOM = 0.4f

        const val THRESHOLD_DISTANCE_FOR_UPDATE = 0.1 // km
    }
}