package com.bestapp.rice.data.model.remote

/**
 * @param locationAddress 주소지
 * @param locationLat 위도
 * @param locationLong 경도
 */
data class PlaceLocation(
    val locationAddress: String,
    val locationLat: String,
    val locationLong: String,
)
