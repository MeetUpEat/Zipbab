package com.bestapp.rice.data.model.remote

/**
 * @params userDocumentId 유저 고유 ID
 * @params nickName 닉네임
 * @params id 아이디
 * @params pw 비밀번호
 * @params profileImage 프로필 이미지
 * @params temperature 매너 온도
 * @params meetingCount 모임 횟수
 * @params posts 게시물
 * @params location 유저 위치(주소, 위도, 경도)
 */
data class User(
    val userDocumentID: String,
    val nickName: String,
    val id: String,
    val pw: String,
    val profileImage: String,
    val temperature: Double,
    val meetingCount: Int,
    val posts: List<Post>,
    val placeLocation: PlaceLocation,
)