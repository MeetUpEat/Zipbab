package com.bestapp.rice.data.model.remote

/**
 *
 * @param meetingDocumentID 미팅 고유 ID
 * @param title 제목
 * @param titleImage 대표 사진 1개
 * @param placeLocation  위치 class
 * @param time 모임 시간
 * @param recruits 모집 인원
 * @param description 상세 설명
 * @param mainMenu 파스타, 전, 구이, 샌드위치
 * @param costValueByPerson 1인당 참여 비용 실제 값 ex. 14500, 20000
 * @param costTypeByPerson 1인당 참여 비용 타입 ex. 1(~3만원), 2(3~5만원), 3, 4
 * @param hostUserDocumentID 호스트의 userDocumentID
 * @param hostTemperature 호스트 온도
 * @param members  참여자 tokenId 리스트
 * @param pendingMembers 대기자 tokenId 리스트
 * @param attendanceCheck 출석 체크한 tokenId 리스트
 * @param activation 모임 활성화 유무, 끝난 상태인지 아닌지 - 검색
 */
// TODO - 7. 뒤에 Response, Entity를 붙여주면 좋겠습니다. 어떤 역할을 하는지 모르겠습니다.
// TODO - 8. 위 주석에서 param이 아닌 property 입니다.
data class MeetingResponse(
    val meetingDocumentID: String,
    val title: String?,
    val titleImage: String?,
    val placeLocation: PlaceLocation,
    val time: String,
    val recruits: Int,
    val description: String,
    val mainMenu: String,
    val costValueByPerson: Int,
    val costTypeByPerson: Int,
    val hostUserDocumentID: String,
    val hostTemperature: Double,
    val members: List<String>,
    val pendingMembers: List<String>,
    val attendanceCheck: List<String>,
    val activation: Boolean,
) {
    // TODO - 9. 아래 constructor 대신 더 좋은 방법은 없을까요?

    // notice: Firebase의 toObject 메서드를 사용하려면 class의 인자가 없는 기본 생성자를 필요로 하여 추가함
    constructor() : this(
        "", "", "", PlaceLocation(), "", 0, "", "",
        0, 0, "", 0.0, emptyList(), emptyList(), emptyList(), true
    )
}


// TODO - 10. moshi nullable을 없애는 방법은 wrapping 과정에서 orEmpty를 쓰는 것 입니다.
//  또는 elvis 연산자로 대응하면 됩니다.
//  튜터 님은 data -> domain 레이어로 넘어갈 때 변환하심.
data class MeetingEntity(
    val title: String,
    val titleImage: String,
    val hostTemperature: Int,
)

fun MeetingResponse.toEntity() = MeetingEntity(
    title = title.orEmpty(),
    titleImage = titleImage.orEmpty(),
    hostTemperature = kotlin.runCatching {
        hostTemperature.
    }
)