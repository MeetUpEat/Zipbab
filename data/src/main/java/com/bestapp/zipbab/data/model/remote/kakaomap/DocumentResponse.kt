package com.bestapp.zipbab.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @property addressResponse 지명 주소지 정보
 * @property roadAddressResponse 도로명 주소지 정보
 * @property addressName 전체 지번 주소 또는 전체 도로명 주소, 입력에 따라 결정됨
 * @property addressType REGION(지명), ROAD(도로명), REGION_ADDR(지번 주소), ROAD_ADDR(도로명 주소)
 * @property longitude 경도 127,xxxxxxxxxxx
 * @property latitude 위도 37.xxxxxxxxxxxxx
 */
@JsonClass(generateAdapter = true)
data class DocumentResponse(
    @field:Json(name = "address") val addressResponse: AddressResponse?,
    @Json(name = "x") val longitude: String?,
    @Json(name = "y") val latitude: String?,
    @Json(name = "address_name") val addressName: String?,
    @Json(name = "address_type") val addressType: String?,
    @field:Json(name = "road_address") val roadAddressResponse: RoadAddressResponse?,
)