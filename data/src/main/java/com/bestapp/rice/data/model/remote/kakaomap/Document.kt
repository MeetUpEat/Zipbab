package com.bestapp.rice.data.model.remote.kakaomap

/**
 * @param address 지명 주소지 정보
 * @param road_address 도로명 주소지 정보
 * @param address_name 전체 지번 주소 또는 전체 도로명 주소, 입력에 따라 결정됨
 * @param address_type REGION(지명), ROAD(도로명), REGION_ADDR(지번 주소), ROAD_ADDR(도로명 주소)
 * @param x 경도 127,xxxxxxxxxxx
 * @param y 위도 37.xxxxxxxxxxxxx
 */
data class Document(
    val address: Address,
    val address_name: String,
    val address_type: String,
    val road_address: Any,
    val x: String,
    val y: String
)