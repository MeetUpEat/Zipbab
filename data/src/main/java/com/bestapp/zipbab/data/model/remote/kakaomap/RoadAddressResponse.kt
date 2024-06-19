package com.bestapp.zipbab.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @param addressName    전체 도로명 주소, 경기 시흥시 봉우재로23번안길 7-1
 * @param buildingName 건물 이름, ""
 * @param mainBuildingNo 건물 본번, 7
 * @param subBuildingNo 건물 부번, 없을 경우 빈 문자열("") 반환, 1
 * @param regionDepthName1 지역명1, 경기
 * @param regionDepthName2 지역명2, 시흥시
 * @param regionDepthName3 지역명3, 정왕동
 * @param roadName 봉우재로23번안길
 * @param zoneNo 지하 여부(Y 또는 N), N
 * @param undergroundYn 우편번호(5자리), 15056
 * @param x 경도 127,xxxxxxxxxxx
 * @param y 위도 37.xxxxxxxxxxxxx
 */
@JsonClass(generateAdapter = true)
data class RoadAddressResponse(
    @Json(name = "address_name") val addressName: String,
    @Json(name = "x") val longitude: String,
    @Json(name = "y") val latitude: String,
    @Json(name = "region_1depth_name") val regionDepthName1: String,
    @Json(name = "region_2depth_name") val regionDepthName2: String,
    @Json(name = "region_3depth_name") val regionDepthName3: String,
    @Json(name = "road_name") val roadName: String,
    @Json(name = "underground_yn") val undergroundYn: String,
    @Json(name = "main_building_no") val mainBuildingNo: String,
    @Json(name = "sub_building_no") val subBuildingNo: String,
    @Json(name = "building_name") val buildingName: String,
    @Json(name = "zone_no") val zoneNo: String,
)