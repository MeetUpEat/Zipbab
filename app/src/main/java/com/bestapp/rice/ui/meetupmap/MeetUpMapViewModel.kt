package com.bestapp.rice.ui.meetupmap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.model.args.toArg
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetUpMapViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {

    private val _meetingUiState = MutableStateFlow<MeetingUiState>(MeetingUiState())
    val meetingUiState: SharedFlow<MeetingUiState> = _meetingUiState.asStateFlow()

    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState())
    val userUiState: SharedFlow<UserUiState> = _userUiState.asStateFlow()

    private val _meetUpMapUiState = MutableStateFlow<MeetUpMapUiState>(MeetUpMapUiState())
    val meetUpMapUiState: SharedFlow<MeetUpMapUiState> = _meetUpMapUiState.asStateFlow()

    fun getMeetings() {
        viewModelScope.launch {
            val meetings = meetingRepository.getMeetings().filter {
                val latlng = LatLng.from(
                    it.placeLocation.locationLat.toDouble(),
                    it.placeLocation.locationLong.toDouble()
                )

                val distance = haversine(DEFAULT_LATLNG, latlng)
                Log.e("내 근처 20km 이내 모임 리스트", "$distance km, $it")

                distance <= DISTANCE_FILTER
            }

            _meetUpMapUiState.value = MeetUpMapUiState(
                meetUpMapMeetingUis = meetings.map {
                    it.toArg()
                }
            )
        }
    }

    companion object {
        val DEFAULT_MEETING_DOCUMENT_ID = "O84eyapKdqIgbjitZZIr"
        val DEFAULT_USER_DOCUMENT_ID = "yUKL3rt0geiVdQJMOeoF"

        // TODO : 사용자가 위치 권한 활성화 시, 사용자 위치 값으로 대체돼야함
        val DEFAULT_LATLNG = LatLng.from(37.4793455, 126.8885391)

        // 사용자 위치 기반 탐색 가능한 거리 -> TODO : ui에서 filter를 통해 바꿀 수 있도록 해야함
        val DISTANCE_FILTER = 20.0
    }
}