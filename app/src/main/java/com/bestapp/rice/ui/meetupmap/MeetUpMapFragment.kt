package com.bestapp.rice.ui.meetupmap

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestapp.rice.userlocation.LocationViewModel
import com.bestapp.rice.databinding.FragmentMeetUpMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnMapViewInfoChangeListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapViewInfo
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MeetUpMapFragment : Fragment() {
    private val viewModel: MeetUpMapViewModel by viewModels()
    private val locationViewModel : LocationViewModel by viewModels()

    private var _binding: FragmentMeetUpMapBinding? = null
    private val binding: FragmentMeetUpMapBinding
        get() = _binding!!

    private var _meetUpListAdapter: MeetUpListAdapter? = null
    private val meetUpListAdapter: MeetUpListAdapter
        get() = _meetUpListAdapter!!

    private var _map: KakaoMap? = null
    private val map: KakaoMap
        get() = _map!!

    private lateinit var userLabel: Label
    private lateinit var meetingLabels: List<Label>

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private val mapLifeCycleCallback = object : MapLifeCycleCallback() {
        // 지도 API 가 정상적으로 종료될 때 호출됨
        override fun onMapDestroy() {
            Log.e(TAG, "onMapDestroy")
        }

        // 지도 API 가 정상적으로 종료될 때 호출됨
        override fun onMapError(p0: Exception?) {
            Log.e(TAG, "onMapError")
        }
    }

    private val onMapViewInfoChangeListener = object : OnMapViewInfoChangeListener {
        // MapType 변경 성공 시 호출
        override fun onMapViewInfoChanged(mapViewInfo: MapViewInfo) {
            Log.e(TAG, "onMapViewInfoChanged")
        }

        // MapType 변경 실패 시 호출
        override fun onMapViewInfoChangeFailed() {
            Log.e(TAG, "onMapViewInfoChangeFailed")
        }
    }

    private val kakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        // Auth 인증 후 API 가 정상적으로 실행될 때 호출됨
        override fun onMapReady(kakaoMap: KakaoMap) {
            _map = kakaoMap
            Log.e(TAG, "onMapReady")

            kakaoMap.changeMapViewInfo(MapViewInfo.from("openmap", MapType.NORMAL));
            kakaoMap.setOnMapViewInfoChangeListener(onMapViewInfoChangeListener)

            initObserve()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetUpMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            }

            else -> {
                Toast.makeText(requireContext(), "위치 권한이 없어서 근처 모임 정보를 제공할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO : meetingDocumentID 받아와서 넣어주기
        viewModel.getMeetings()

        binding.mv.start(mapLifeCycleCallback, kakaoMapReadyCallback)

        binding.layout.fabGps.setOnClickListener {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            locationViewModel.startGetLocation()

            // TODO : 권한 요청에 대한 결과 처리 추가해야함
        }

        initBottomSheet()
    }

    private fun initObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.locationState.collect {
                if (!::userLabel.isInitialized) {
                    userLabel = map.createUserLabel(requireContext(), it)
                    map.moveToCamera(it)
                }

                // TODO: 트래킹 활성화 시에 카메라가 계속 이동되도록 할 수 있음
                userLabel.moveTo(it)
                // map.moveToCamera(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetUpMapUiState.collectLatest {
                meetingLabels = map.createMeetingLabels(requireContext(), it)
                Log.d("20km 내의 미팅 개수 등", "${meetingLabels.size}개, $meetingLabels")

                val onLabelClickListener = object: KakaoMap.OnLabelClickListener {
                    override fun onLabelClicked(map: KakaoMap?, labelLayer: LabelLayer?, label: Label?) {
                        Log.d("라벨 클릭됨", label.toString())

                        // TODO 바텀 시트내의 메인 모임을 클릭된 label의 데이터로 심어줘야함
                        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }
                map.setOnLabelClickListener(onLabelClickListener)
            }
        }
    }

    /**
     *   public static final int STATE_DRAGGING = 1;
     *   public static final int STATE_SETTLING = 2;
     *   public static final int STATE_EXPANDED = 3;
     *   public static final int STATE_COLLAPSED = 4;
     *   public static final int STATE_HIDDEN = 5;
     *   public static final int STATE_HALF_EXPANDED = 6;
     */
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            Log.d("state check", newState.toString())
        }

        /**
         *  slideOffset : -1.0 ~ 1.0 범위
         *  -1 : 완전히 숨겨짐 - STATE_HIDDEN
         *  0 : 중간쯤 펼쳐짐 - STATE_HALF_EXPANDED
         *  1 : 완전히 펼쳐짐 - STATE_EXPANDED
         */
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            Log.d("slide state check", slideOffset.toString())
        }
    }

    /** Behavior 상태
     *  STATE_EXPANDED : 완전히 펼쳐진 상태            1.0
     *  STATE_HALF_EXPANDED : 절반으로 펼쳐진 상태     0.5
     *  STATE_COLLAPSED : 접혀있는 상태                 0
     *  STATE_HIDDEN : 아래로 숨겨진 상태 (보이지 않음)  -1
     *  STATE_DRAGGING : 드래깅되고 있는 상태
     *  STATE_SETTLING : 드래그/스와이프 직후 고정된 상태
     */
    private fun initBottomSheet() {
        // 바텀 시트
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.layout.bsMeetings)
        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        standardBottomSheetBehavior.setPeekHeight(300, true) // 접혀있는 상태(STATE_COLLAPSED)일 때의 고정 높이 지정
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED // 초기 세팅: 절반만 확장된 상태

        _meetUpListAdapter = MeetUpListAdapter { position ->
            Log.d("position", position.toString())
            selectedMeeingItem(position)
        }

        binding.layout.rv.adapter = meetUpListAdapter
        binding.layout.rv.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetUpMapUiState.collectLatest {
                meetUpListAdapter.submitList(it.meetUpMapMeetingUis)
            }
        }
    }

    private fun selectedMeeingItem(position: Int) {
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // TODO: meetingLabels 라벨들 생성 시간이 오래 걸려서 index 오류 방지를 위해 if문 추가
        if (meetingLabels.size > position) {
            map.moveToCamera(meetingLabels[position].position)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mv.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mv.pause()
    }


    override fun onDestroyView() {
        _binding = null
        _map = null
        standardBottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)

        super.onDestroyView()
    }

    companion object {
        const val TAG = "KakaoMap lifecycle 테스트"
        const val ZERO = 0.0

        const val UPDATE_CAMERA_POSTIION_TIME = 500
        const val DEFAULT_ZOOM_LEVEL = 17

        const val IMAGE_URI =
            "https://firebasestorage.googleapis.com/v0/b/food-879fc.appspot.com/o/images%2F1717358591361.jpg?alt=media&token=e8dff2f2-3327-460a-9c9a-8b13f4e4607c"

        val POS_KAKAO = LatLng.from(37.39334413781196, 127.11482638384224)
    }
}