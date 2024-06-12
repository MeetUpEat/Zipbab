package com.bestapp.rice.ui.meetupmap

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MeetUpMapFragment : Fragment() {
    private val viewModel: MeetUpMapViewModel by viewModels()
    private val locationViewModel : LocationViewModel by viewModels()

    private var _binding: FragmentMeetUpMapBinding? = null
    private val binding: FragmentMeetUpMapBinding
        get() = _binding!!

    private var _map: KakaoMap? = null
    private val map: KakaoMap
        get() = _map!!

    private lateinit var userLabel: Label
    private lateinit var meetingLabels: List<Label>

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

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Do something for slide offset.
        }
    }

    private val kakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        // Auth 인증 후 API 가 정상적으로 실행될 때 호출됨
        override fun onMapReady(kakaoMap: KakaoMap) {
            _map = kakaoMap
            Log.e(TAG, "onMapReady")

            kakaoMap.changeMapViewInfo(MapViewInfo.from("openmap", MapType.NORMAL));
            kakaoMap.setOnMapViewInfoChangeListener(onMapViewInfoChangeListener)

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
                viewModel.meetUpMapUiState.collect {
                    meetingLabels = map.createMeetingLabels(requireContext(), it)
                    Log.d("20km 내의 미팅 개수 등", "${meetingLabels.size}개, $meetingLabels")

                    val onLabelClickListener = object: KakaoMap.OnLabelClickListener {
                        override fun onLabelClicked(map: KakaoMap?, labelLayer: LabelLayer?, label: Label?) {
                            Log.d("라벨 클릭됨", label.toString())

                            // TODO 바텀 시트 확장 및 바텀 시트내의 메인 모임을 클릭된 label의 데이터로 심어줘야함 -> How...?

                        }
                    }
                    map.setOnLabelClickListener(onLabelClickListener)
                }
            }
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

        binding.fabGps.setOnClickListener {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            locationViewModel.startGetLocation()

            // TODO : 권한 요청에 대한 결과 처리 추가해야함
        }

        // 바텀 시트
        val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.layout.bsMeetings)

        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        standardBottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
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