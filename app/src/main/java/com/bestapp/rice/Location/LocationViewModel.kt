package com.bestapp.rice.Location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationService: LocationService,
) : ViewModel() {

    private val _locationState = MutableStateFlow<LatLng>(DEFAULT_LOCATION_LATLNG)
    val locationState: StateFlow<LatLng> = _locationState

    init {
        viewModelScope.launch {
            locationService.requestLocationUpdates().collect { location ->
                if (location == null) {
                    return@collect
                }

                _locationState.value = LatLng.from(
                    location!!.latitude, location!!.longitude
                )
            }
        }
    }

    companion object {
        val DEFAULT_LOCATION_LATLNG = LatLng.from(37.39334413781196, 127.11482638384224)
    }
}