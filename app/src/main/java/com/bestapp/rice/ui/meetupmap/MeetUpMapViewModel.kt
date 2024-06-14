package com.bestapp.rice.ui.meetupmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.userlocation.LocationService
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetUpMapViewModel @Inject constructor(
    private val locationService: LocationService,
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {

    private val _userNickname = MutableSharedFlow<String>()
    val userNickname: SharedFlow<String> = _userNickname.asSharedFlow()

    private val _isLocationPermissionGranted = MutableSharedFlow<Boolean>()
    val isLocationPermissionGranted: SharedFlow<Boolean> = _isLocationPermissionGranted.asSharedFlow()

    private val _userLocationState = MutableSharedFlow<LatLng>()
    val userLocationState: SharedFlow<LatLng> = _userLocationState.asSharedFlow()

    private val _userLabel = MutableStateFlow<Label?>(null)

    private val _meetingLabels = MutableStateFlow<List<Label>>(emptyList())

    private val _meetUpMapUiState = MutableStateFlow<MeetUpMapUiState>(MeetUpMapUiState())

    val meetUpMapUiState: SharedFlow<MeetUpMapUiState> = _meetUpMapUiState.asStateFlow()

    suspend fun setRequestPermissionResult(isLocationAllGranted: Boolean) {
        _isLocationPermissionGranted.emit(isLocationAllGranted)
    }

    fun requestLocation() {
        viewModelScope.launch {
            val userLocation = locationService.requestLocation()

            if (userLocation != null) {
                _userLocationState.emit(
                    LatLng.from(userLocation.latitude, userLocation.longitude)
                )
            }
        }
    }

    fun updateUserLabel(map: KakaoMap, latLng: LatLng) {
        if (_userLabel.value == null) {
            _userLabel.value = map.updateUserLabel(latLng)
        }

        getMeetings(latLng)
        map.moveToCamera(latLng)
    }

    fun removeUserLabel() {
        _userLabel.value = null
    }

//    fun setMeetingLabels(labels: List<Label>) {
//        _meetingLabels.value = labels
//    }

    fun getUserNickname() {
        viewModelScope.launch {
            // TODO: Merge 하기 전에 ifEmpty 삭제할 것.
            val userDocumentedID = getUser()


            val userNickname = if (userDocumentedID.isNotEmpty()) {
                userRepository.getUser(userDocumentedID).nickname
            } else {
                NO_LOGIN_USER_DEFAULT_NICKNAME
            }

            _userNickname.emit(userNickname)
        }
    }
    private suspend fun getUser() = appSettingRepository.userPreferencesFlow.first()

    private fun getMeetings(latLngUser: LatLng) {
        viewModelScope.launch {
            val meetings = meetingRepository.getMeetings()

            val meetUpMapUiState = MeetUpMapUiState(
                meetUpMapMeetingUis = meetings.map {
                    val latlng = LatLng.from(
                        it.placeLocation.locationLat.toDouble(),
                        it.placeLocation.locationLong.toDouble()
                    )

                    val distance = haversine(latLngUser, latlng)
                    it.toUi(distance)
                }.filter {
                    it.distanceByUser <= DISTANCE_FILTER
                }.sortedBy {
                    it.distanceByUser
                }
            )

            _meetUpMapUiState.value = meetUpMapUiState
        }
    }

    companion object {
        // TODO : MVP 이후, ui에서 filter를 통해 선택할 수 있도록 제공
        const val DISTANCE_FILTER = 20.0 // 사용자 위치 기반 탐색 가능한 거리
        const val NO_LOGIN_USER_DEFAULT_NICKNAME = "익명의 사용자"
    }
}