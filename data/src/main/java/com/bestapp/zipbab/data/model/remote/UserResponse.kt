package com.bestapp.zipbab.data.model.remote

/**
 * @property userDocumentID 유저 고유 ID
 * @property nickname 닉네임
 * @property id 아이디
 * @property pw 비밀번호
 * @property profileImage 프로필 이미지
 * @property temperature 매너 온도
 * @property meetingCount 모임 횟수
 * @property posts 게시물에 포함된 사진 주소들
 * @property placeLocation 유저 위치(주소, 위도, 경도)
 */
data class UserResponse(
    val userDocumentID: String,
    val uuid: String,
    val nickname: String,
    val id: String,
    val pw: String,
    val profileImage: String,
    val temperature: Double,
    val meetingCount: Int,
    val notificationList: List<NotificationTypeResponse>,
    val meetingReviews: List<String>,
    val posts: List<String>,
    val placeLocation: PlaceLocation,
) {
    companion object {
        val empty = UserResponse(
            userDocumentID = "",
            uuid = "",
            nickname = "",
            id = "",
            pw = "",
            profileImage = "",
            temperature = 0.0,
            meetingCount = 0,
            notificationList = listOf(),
            meetingReviews = listOf(),
            posts = listOf(),
            placeLocation = PlaceLocation(
                locationAddress = "",
                locationLat = "",
                locationLong = ""
            )
        )
    }
    // notice: Firebase의 toObject 메서드를 사용하려면 class의 인자가 없는 기본 생성자를 필요로 하여 추가함
    constructor() : this("", "", "", "", "", "", 0.0, 0, emptyList(), emptyList(), emptyList(), PlaceLocation())
}