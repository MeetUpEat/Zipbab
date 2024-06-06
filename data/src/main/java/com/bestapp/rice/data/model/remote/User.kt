package com.bestapp.rice.data.model.remote

/**
 * @params userDocumentId 유저 고유 ID
 * @params nickname 닉네임
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
    val nickname: String,
    val id: String,
    val pw: String,
    val profileImage: Image,
    val temperature: Double,
    val meetingCount: Int,
    val posts: List<Post>,
    val placeLocation: PlaceLocation,
) {
    companion object {
        val empty = User(
            userDocumentID = "",
            nickname = "",
            id = "",
            pw = "",
            profileImage = Image(""),
            temperature = 0.0,
            meetingCount = 0,
            posts = listOf(),
            placeLocation = PlaceLocation(
                locationAddress = "",
                locationLat = "",
                locationLong = ""
            )
        )
    }
    // notice: Firebase의 toObject 메서드를 사용하려면 class의 인자가 없는 기본 생성자를 필요로 하여 추가함
    constructor() : this("", "", "", "", "", 0.0, 0, emptyList(), PlaceLocation())
}