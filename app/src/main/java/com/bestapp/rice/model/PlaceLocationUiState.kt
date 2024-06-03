package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.PlaceLocation
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceLocationUiState(
    val locationAddress: String,
    val locationLat: String,
    val locationLong: String,
) : Parcelable {

    fun toData() = PlaceLocation(
        locationAddress = locationAddress,
        locationLat = locationLat,
        locationLong = locationLong
    )

    companion object {
        fun createFrom(placeLocation: PlaceLocation) = PlaceLocationUiState(
            locationAddress = placeLocation.locationAddress,
            locationLat = placeLocation.locationLat,
            locationLong = placeLocation.locationLong,
        )
    }
}
