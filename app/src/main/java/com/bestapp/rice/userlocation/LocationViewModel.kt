package com.bestapp.rice.userlocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationService: LocationService,
) : ViewModel() {

    private val _userLocationState = MutableSharedFlow<LatLng>()
    val userLocationState: SharedFlow<LatLng> = _userLocationState

    fun startGetLocation() {
        viewModelScope.launch {
            locationService.requestLocationUpdates().collect { location ->
                if (location != null) {
                    _userLocationState.emit(
                        LatLng.from(location.latitude, location.longitude)
                    )
                }
            }
        }
    }

    companion object {
        val DEFAULT_LOCATION_LATLNG = LatLng.from(37.39334413781196, 127.11482638384224)
    }
}