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
    val userDocumentID: String = "",
    val nickname: String = "",
    val id: String = "",
    val pw: String = "",
    val profileImage: String = "",
    val temperature: Double = 0.0,
    val meetingCount: Int = 0,
    val notifications: List<NotificationTypeResponse> = emptyList(),
    val meetingReviews: List<String> = emptyList(),
    val posts: List<String> = emptyList(),
    val placeLocation: PlaceLocation = PlaceLocation()
)