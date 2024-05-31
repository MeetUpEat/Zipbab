package com.bestapp.rice.data.model.remote.kakaomap

/**
 * @param address_name 전체 지번 주소, 경기 시흥시 정왕동 1614-11
 * @param b_code 법정 코드, 4139013200
 * @param h_code 행정 코드, 4139059100
 * @param main_address_no 지번 주번지, 1614
 * @param sub_address_no 지번 부번지, 없을 경우 빈 문자열("") 반환, 11
 * @param mountain_yn 산 여부(Y 또는 N), N
 * @param region_1depth_name 지역 1 Depth(시도 단위), 경기
 * @param region_2depth_name 지역 2 Depth(구 단위), 시흥시
 * @param region_3depth_h_name 지역 3 Depth(동 단위), 정왕1동
 * @param region_3depth_name 지역 3 Depth(행정동 명칭), 정왕동
 * @param x 경도 127,xxxxxxxxxxx
 * @param y 위도 37.xxxxxxxxxxxxx
 */
data class Address(
    val address_name: String,
    val b_code: String,
    val h_code: String,
    val main_address_no: String,
    val mountain_yn: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_h_name: String,
    val region_3depth_name: String,
    val sub_address_no: String,
    val x: String,
    val y: String
)