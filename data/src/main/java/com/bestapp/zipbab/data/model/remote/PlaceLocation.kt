package com.bestapp.zipbab.data.model.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @param locationAddress 주소지
 * @param locationLat 위도
 * @param locationLong 경도
 */
@Parcelize
data class PlaceLocation(
    val locationAddress: String,
    val locationLat: String,
    val locationLong: String,
) : Parcelable{
    constructor() : this("", "", "")
}
