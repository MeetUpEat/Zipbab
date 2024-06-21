package com.bestapp.zipbab.data.model.remote

/**
 * @property locationAddress 주소지
 * @property locationLat 위도
 * @property locationLong 경도
 */
data class PlaceLocation(
    val locationAddress: String = "",
    val locationLat: String = "",
    val locationLong: String = "",
)