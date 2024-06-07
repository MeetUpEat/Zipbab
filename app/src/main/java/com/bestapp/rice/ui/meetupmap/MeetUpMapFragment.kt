package com.bestapp.rice.ui.meetupmap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bestapp.rice.databinding.FragmentMeetUpMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnMapViewInfoChangeListener
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapViewInfo

class MeetUpMapFragment : Fragment() {

    private var _binding: FragmentMeetUpMapBinding? = null
    private val binding: FragmentMeetUpMapBinding
        get() = _binding!!

    val mapLifeCycleCallback = object : MapLifeCycleCallback() {
        // 지도 API 가 정상적으로 종료될 때 호출됨
        override fun onMapDestroy() {
            Log.e(TAG, "onMapDestroy")
        }

        // 지도 API 가 정상적으로 종료될 때 호출됨
        override fun onMapError(p0: Exception?) {
            Log.e(TAG, "onMapError")
        }
    }

    val kakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        // 인증 후 API 가 정상적으로 실행될 때 호출됨
        override fun onMapReady(kakaoMap: KakaoMap) {
            Log.e(TAG, "onMapReady")

            kakaoMap.changeMapViewInfo(MapViewInfo.from("openmap", MapType.NORMAL));

            kakaoMap.setOnMapViewInfoChangeListener(object : OnMapViewInfoChangeListener {
                // MapType 변경 성공 시 호출
                override fun onMapViewInfoChanged(mapViewInfo: MapViewInfo) {
                    Log.e(TAG, "onMapViewInfoChanged")
                }

                // MapType 변경 실패 시 호출
                override fun onMapViewInfoChangeFailed() {
                    Log.e(TAG, "onMapViewInfoChangeFailed")
                }
            })
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

    companion object {
        const val TAG = "KakaoMap lifecycle 테스트"
    }
}