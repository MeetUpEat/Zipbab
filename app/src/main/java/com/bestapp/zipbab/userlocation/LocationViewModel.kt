package com.bestapp.zipbab.userlocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationService: LocationService,
) : ViewModel() {

    private val _userLocationState = MutableSharedFlow<LatLng>()
    val userLocationState: SharedFlow<LatLng> = _userLocationState

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

    companion object {
        val DEFAULT_LOCATION_LATLNG = LatLng.from(37.39334413781196, 127.11482638384224)
    }
}