package com.bestapp.zipbab.ui.meetupmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.Meeting
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.userlocation.LocationService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _isLocationPermissionGranted = MutableSharedFlow<Boolean>(replay = 1)
    val isLocationPermissionGranted: SharedFlow<Boolean> = _isLocationPermissionGranted.asSharedFlow()

    private val _userLocationState = MutableSharedFlow<LatLng>()
    val userLocationState: SharedFlow<LatLng> = _userLocationState.asSharedFlow()

    private val _userMarker = MutableStateFlow<Marker?>(null)

    private val _meetingMarkers = MutableStateFlow<List<Marker>>(emptyList())

    private val _meetUpMapUiState = MutableStateFlow<MeetUpMapUiState>(MeetUpMapUiState())

    val meetUpMapUiState: SharedFlow<MeetUpMapUiState> = _meetUpMapUiState.asStateFlow()

    fun setRequestPermissionResult(isLocationAllGranted: Boolean) {
        viewModelScope.launch {
            _isLocationPermissionGranted.emit(isLocationAllGranted)
        }
    }

    fun setLocation(latLngUser: LatLng) {
        viewModelScope.launch {
            _userLocationState.emit(latLngUser)
        }
    }

    fun updateUserMarker(map: NaverMap, latLng: LatLng) {

        getMeetings(latLng)
    }

    fun setMeetingLabels(labels: List<Marker>) {
        _meetingMarkers.value = labels
    }

    fun getUserNickname() {
        viewModelScope.launch {
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
                    it.toUiWithDistance(latLngUser)
                }.filter {
                    it.distance <= DISTANCE_FILTER
                }.sortedBy {
                    it.distance
                }.map {
                    it.addFormatDistance()
                }
            )

            _meetUpMapUiState.value = meetUpMapUiState
        }
    }

    private fun Meeting.toUiWithDistance(latLngUser: LatLng): MeetUpMapUi {
        val latlng = LatLng(
            placeLocation.locationLat.toDouble(),
            placeLocation.locationLong.toDouble()
        )
        val distance = haversine(latLngUser, latlng)

        return toUi(distance)
    }

    private fun MeetUpMapUi.addFormatDistance(): MeetUpMapUi {
        val distanceByUser = if (distance < CLASSIFICATION_STANDARD_VALUE) {
            DISTANCE_METER.format(distance * UNIT_CONVERSION_MAPPER)
        } else {
            DISTANCE_KILOMETER.format(distance)
        }
        return copy(distanceByUser = distanceByUser)
    }

    companion object {
        const val NO_LOGIN_USER_DEFAULT_NICKNAME = "익명의 사용자"

        // TODO : MVP 이후, ui에서 filter를 통해 선택할 수 있도록 제공
        const val DISTANCE_FILTER = 20.0 // 사용자 위치 기반 탐색 가능한 거리

        const val CLASSIFICATION_STANDARD_VALUE = 1.0
        const val UNIT_CONVERSION_MAPPER = 1000
        const val DISTANCE_METER = "%.0fm"
        const val DISTANCE_KILOMETER = "%.1fkm"

    }
}