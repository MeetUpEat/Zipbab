package com.bestapp.rice.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceLocationUiState(
    val locationAddress: String,
    val locationLat: String,
    val locationLong: String,
) : Parcelable