package com.bestapp.rice.ui.meetupmap

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bestapp.rice.databinding.FragmentMeetUpMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnMapViewInfoChangeListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapViewInfo
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelIconStyle
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MeetUpMapFragment : Fragment() {
    private val viewModel: MeetUpMapViewModel by viewModels()

    private var _binding: FragmentMeetUpMapBinding? = null
    private val binding: FragmentMeetUpMapBinding
        get() = _binding!!

    lateinit var bitmap : Bitmap

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

            // LabelStyles : 지도의 확대/축소 줌레벨 마다 각각 다른 LabelStyle을 적용할 수 있음
            // Min ZoomLevel ~ 7 까지   : 스타일 안나옴
            // 8 ~ 10 까지              : red_marker 이미지 나옴
            // 11 ~ 14 까지             : blue_marker 이미지 나옴
            // 15 ~ Max ZoomLevel 까지  : blue_marker 이미지와 텍스트 나옴
            var styles = LabelStyles.from(
                "myStyleId",
                LabelStyle.from(R.drawable.btn_star_big_off).setZoomLevel(8),
                LabelStyle.from(R.drawable.btn_star_big_off).setZoomLevel(11),
                LabelStyle.from(R.drawable.btn_star_big_off)
                    .setTextStyles(32, Color.BLACK, 1, Color.GRAY)
                    .setApplyDpScale(false)
                    .setZoomLevel(15)
            )

            // 라벨 스타일 추가
            styles = kakaoMap.labelManager!!.addLabelStyles(styles!!)

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.meetUpMapUiState.collect { meetUpMapUiState ->
                    meetUpMapUiState.meetUpMapUserUis.forEach {
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
                Log.e(TAG, "meetingUiState")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            bitmap = toBitmap(requireContext(), IMAGE_URI)!!

            viewModel.meetUpMapUiState.collect {
                binding.mv.start(mapLifeCycleCallback, kakaoMapReadyCallback)
                Log.e(TAG, "meetUpMapUiState")
            }
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

        super.onDestroyView()
    }

    suspend fun toBitmap(context: Context, uri: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(uri)
                .build()

            val result = (loader.execute(request) as? SuccessResult)?.drawable
            (result as? BitmapDrawable)?.bitmap
        }
    }

    companion object {
        const val TAG = "KakaoMap lifecycle 테스트"
        const val IMAGE_URI = "https://firebasestorage.googleapis.com/v0/b/food-879fc.appspot.com/o/images%2F1717358591361.jpg?alt=media&token=e8dff2f2-3327-460a-9c9a-8b13f4e4607c"

        val POS_KAKAO = LatLng.from(37.39334413781196, 127.11482638384224)
    }
}