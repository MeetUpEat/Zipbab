package com.bestapp.rice.ui.meetupmap

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bestapp.rice.databinding.FragmentMeetUpMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnMapViewInfoChangeListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapViewInfo
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeetUpMapFragment : Fragment() {
    private val viewModel: MeetUpMapViewModel by viewModels()

    private var _binding: FragmentMeetUpMapBinding? = null
    private val binding: FragmentMeetUpMapBinding
        get() = _binding!!

    lateinit var bitmap: Bitmap

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
        // 인증 후 API 가 정상적으로 실행될 때 호출됨
        override fun onMapReady(kakaoMap: KakaoMap) {
            Log.e(TAG, "onMapReady")

            kakaoMap.changeMapViewInfo(MapViewInfo.from("openmap", MapType.NORMAL));
            kakaoMap.setOnMapViewInfoChangeListener(onMapViewInfoChangeListener)

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.meetUpMapUiState.collect { meetUpMapUiState ->
                    meetUpMapUiState.meetUpMapUserUis.forEach {
                        val bitmap = toBitmap(requireContext(), it.profileImage)
                        val styles = createLabelStyles(bitmap!!)

                        // 라벨 스타일 추가
                        kakaoMap.labelManager!!.addLabelStyles(styles!!)

                        val pos = LatLng.from(
                            it.placeLocationUiState.locationLat.toDouble(),
                            it.placeLocationUiState.locationLong.toDouble()
                        )
                        Log.d("pos", pos.toString())

                        // 라벨 생성
                        val label: Label = kakaoMap.labelManager!!.layer!!.addLabel(
                            LabelOptions.from(pos)
                                .setStyles(styles).setTexts(
                                    it.nickname,
                                    "1.3km"
                                )
                        )
                    }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMeeting("")

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetingUiState.collect {
                viewModel.getUsers(it.members)
            }
        }

        binding.mv.start(mapLifeCycleCallback, kakaoMapReadyCallback)
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

        super.onDestroyView()
    }

    /** LabelStyles : 지도의 확대/축소 줌레벨 마다 각각 다른 LabelStyle을 적용할 수 있음
     * Min ZoomLevel ~ 7 까지,
     * 8 ~ 10 까지              : bitmap 이미지 나옴
     * 11 ~ 14 까지             : bitmap 이미지 나옴
     * 15 ~ Max ZoomLevel 까지  : bitmap 이미지와 텍스트 나옴
     */

    private fun createLabelStyles(bitmap: Bitmap): LabelStyles {
        return LabelStyles.from(
            "myStyleId",
            LabelStyle.from(bitmap).setZoomLevel(8),
            LabelStyle.from(bitmap).setZoomLevel(11)
                .setTextStyles(16, Color.BLACK, 1, Color.GRAY),
            LabelStyle.from(bitmap).setZoomLevel(15)
                .setTextStyles(24, Color.BLACK, 1, Color.GRAY)
        )
    }
    
    companion object {
        const val TAG = "KakaoMap lifecycle 테스트"
        const val IMAGE_URI =
            "https://firebasestorage.googleapis.com/v0/b/food-879fc.appspot.com/o/images%2F1717358591361.jpg?alt=media&token=e8dff2f2-3327-460a-9c9a-8b13f4e4607c"

        val POS_KAKAO = LatLng.from(37.39334413781196, 127.11482638384224)
    }
}