package com.bestapp.zipbab.data.model.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @property locationAddress 주소지
 * @property locationLat 위도
 * @property locationLong 경도
 */
@Parcelize
data class PlaceLocation(
    val locationAddress: String = "",
    val locationLat: String = "",
    val locationLong: String = "",
) : Parcelable
