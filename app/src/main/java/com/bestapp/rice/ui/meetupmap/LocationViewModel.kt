package com.bestapp.rice.ui.meetupmap

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

    private val _locationState = MutableStateFlow<LatLng>(
        LatLng.from(37.39334413781196, 127.11482638384224)
    )
    val locationState: StateFlow<LatLng> = _locationState

    fun startGetLocation() {
        viewModelScope.launch {
            locationService.requestLocationUpdates().collect { location ->
                location.let {
                    _locationState.value = LatLng.from(
                        location!!.latitude, location!!.longitude
                    )
                }
            }
        }
    }
}