package com.bestapp.rice.data.model.remote.kakaomap

/**
 * @param address_name 	전체 도로명 주소, 경기 시흥시 봉우재로23번안길 7-1
 * @param building_name 건물 이름, ""
 * @param main_building_no 건물 본번, 7
 * @param sub_building_no 건물 부번, 없을 경우 빈 문자열("") 반환, 1
 * @param region_1depth_name 지역명1, 경기
 * @param region_2depth_name 지역명2, 시흥시
 * @param region_3depth_name 지역명3, 정왕동
 * @param road_name 봉우재로23번안길
 * @param zone_no 지하 여부(Y 또는 N), N
 * @param underground_yn 우편번호(5자리), 15056
 * @param x 경도 127,xxxxxxxxxxx
 * @param y 위도 37.xxxxxxxxxxxxx
 */
data class RoadAddress(
    val address_name: String,
    val building_name: String,
    val main_building_no: String,
    val sub_building_no: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val road_name: String,
    val underground_yn: String,
    val x: String,
    val y: String,
    val zone_no: String
)