package com.bestapp.rice.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceLocationUi(
    val locationAddress: String = "",
    val locationLat: String = "",
    val locationLong: String = "",
): Parcelable
