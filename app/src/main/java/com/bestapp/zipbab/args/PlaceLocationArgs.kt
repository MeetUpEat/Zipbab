package com.bestapp.zipbab.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceLocationArgs(
    val locationAddress: String = "",
    val locationLat: String = "",
    val locationLong: String = "",
) : Parcelable
