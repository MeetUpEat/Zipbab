package com.bestapp.rice.ui.meetupmap

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentMeetUpMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnMapViewInfoChangeListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapViewInfo
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
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

            viewLifecycleOwner.lifecycleScope.launch {
                locationViewModel.locationState.collect {
                    if (!::userLabel.isInitialized) {
                        userLabel = map.createUserLabel(it)
                        map.moveToCamera(userLabel.position)
                    }

                    // TODO: 트래킹 활성화 시에 카메라가 계속 이동되도록 할 수 있음
                    userLabel.moveTo(it)
                    // map.moveToCamera(it)
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.meetUpMapUiState.collect {
                    createMeetingLabels(it)
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
                // No location access granted.
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

            // TODO : 권한 요청에 대한 결과 처리 추가해야함


            // 내부 로직에서 권한 체크후, 권한이 있을 때만 가져오도록 구현되어 있음
            locationViewModel.startGetLocation()
        }

        val modal = MeetUpModalBottomSheet()
        parentFragmentManager.let { modal.show(it, MeetUpModalBottomSheet.TAG) }
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

    /** 사용자 위치를 파란색 원 아이콘으로 표시하도록 해주는 함수
     *
     */
    private suspend fun KakaoMap.createUserLabel(latLng: LatLng): Label {
        val bitmap = toBitmap(requireContext(), IMAGE_URI)

        var styles = LabelStyles.from(
            "userLocationIcon",
            LabelStyle.from(R.drawable.sample_profile_image).setZoomLevel(10),
            LabelStyle.from(R.drawable.sample_profile_image).setZoomLevel(15)
                .setTextStyles(16, Color.BLACK, 1, Color.GRAY),
            LabelStyle.from(R.drawable.sample_profile_image).setZoomLevel(18)
                .setTextStyles(32, Color.BLACK, 1, Color.GRAY),
        )

        // 라벨 스타일 추가
        styles = this.labelManager!!.addLabelStyles(styles!!)

        val pos = LatLng.from(
            latLng.getLatitude(),
            latLng.getLongitude()
        )

        // 라벨 생성
        return map.labelManager!!.layer!!.addLabel(
            LabelOptions.from(pos)
                .setStyles(styles)
                .setTexts("사용자 위치")
        )
    }

    /** LabelStyles : 지도의 확대/축소 줌레벨 마다 각각 다른 LabelStyle을 적용할 수 있음
     * Min ZoomLevel ~ 7 까지,
     * 8 ~ 10 까지              : bitmap 이미지 나옴
     * 11 ~ 14 까지             : bitmap 이미지 나옴
     * 15 ~ Max ZoomLevel 까지  : bitmap 이미지와 텍스트 나옴
     */

    private suspend fun createMeetingLabels(meetUpMapUiState: MeetUpMapUiState) {
        meetUpMapUiState.meetUpMapMeetingUis.forEach {
            val bitmap = toBitmap(requireContext(), it.titleImage)
            val styles = createLabelStyles(bitmap!!)

            // 라벨 스타일 추가
            map.labelManager!!.addLabelStyles(styles!!)

            val pos = LatLng.from(
                it.placeLocationArg.locationLat.toDouble(),
                it.placeLocationArg.locationLong.toDouble()
            )
            Log.d("pos", pos.toString())

            // 라벨 생성
            map.labelManager!!.layer!!.addLabel(
                LabelOptions.from(pos)
                    .setStyles(styles).setTexts(
                        it.title,
                    )
            )
        }
    }

    private fun KakaoMap.moveToCamera(lat: Double, long: Double) = moveToCamera(LatLng.from(lat, long))

    private fun KakaoMap.moveToCamera(latLng: LatLng) {
        if (latLng.latitude == ZERO && latLng.longitude == ZERO) {
            return
        }

        val cameraUpdatePosition = CameraUpdateFactory.newCenterPosition(latLng, 15)
        val cameraAnimation = CameraAnimation.from(500, true, true)

        this.moveCamera(cameraUpdatePosition, cameraAnimation)
    }

    private fun createLabelStyles(bitmap: Bitmap): LabelStyles {
        return LabelStyles.from(
            "customStyle1",
            LabelStyle.from(bitmap).setZoomLevel(11)
                .setTextStyles(16, Color.BLACK, 1, Color.GRAY),
            LabelStyle.from(bitmap).setZoomLevel(15)
                .setTextStyles(24, Color.BLACK, 1, Color.GRAY)
        )
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