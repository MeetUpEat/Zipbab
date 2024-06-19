package com.bestapp.zipbab.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @param addressName 전체 지번 주소, 경기 시흥시 정왕동 1614-11
 * @param bCode 법정 코드, 4139013200
 * @param hCode 행정 코드, 4139059100
 * @param mainAddressNo 지번 주번지, 1614
 * @param subAddressNo 지번 부번지, 없을 경우 빈 문자열("") 반환, 11
 * @param mountainYn 산 여부(Y 또는 N), N
 * @param regionDepthName1 지역 1 Depth(시도 단위), 경기
 * @param regionDepthName2 지역 2 Depth(구 단위), 시흥시
 * @param regionDepthName3h 지역 3 Depth(동 단위), 정왕1동
 * @param regionDepthName3 지역 3 Depth(행정동 명칭), 정왕동
 * @param longitude 경도 127,x(11자리)
 * @param latitude 위도 37.x(13자리)
 */
@JsonClass(generateAdapter = true)
data class AddressResponse(
    @Json(name = "address_name") val addressName: String,
    @Json(name = "x") val longitude: String,
    @Json(name = "y") val latitude: String,
    @Json(name = "b_code") val bCode: String,
    @Json(name = "h_code") val hCode: String,
    @Json(name = "main_address_no") val mainAddressNo: String,
    @Json(name = "sub_address_no") val subAddressNo: String,
    @Json(name = "mountain_yn") val mountainYn: String,
    @Json(name = "region_1depth_name") val regionDepthName1: String,
    @Json(name = "region_2depth_name") val regionDepthName2: String,
    @Json(name = "region_3depth_h_name") val regionDepthName3h: String,
    @Json(name = "region_3depth_name") val regionDepthName3: String,
)