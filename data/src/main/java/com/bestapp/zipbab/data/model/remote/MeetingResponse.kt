package com.bestapp.zipbab.data.model.remote

/**
 *
 * @property meetingDocumentID 미팅 고유 ID
 * @property title 제목
 * @property titleImage 대표 사진 1개
 * @property placeLocation  위치 class
 * @property time 모임 시간
 * @property recruits 모집 인원
 * @property description 상세 설명
 * @property mainMenu 파스타, 전, 구이, 샌드위치
 * @property costValueByPerson 1인당 참여 비용 실제 값 ex. 14500, 20000
 * @property costTypeByPerson 1인당 참여 비용 타입 ex. 1(~3만원), 2(3~5만원), 3, 4
 * @property hostUserDocumentID 호스트의 userDocumentID
 * @property hostTemperature 호스트 온도
 * @property members  참여자 tokenId 리스트
 * @property pendingMembers 대기자 tokenId 리스트
 * @property attendanceCheck 출석 체크한 tokenId 리스트
 * @property activation 모임 활성화 유무, 끝난 상태인지 아닌지 - 검색
 */
data class MeetingResponse(
    val meetingDocumentID: String,
    val title: String,
    val titleImage: String,
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
    // notice: Firebase의 toObject 메서드를 사용하려면 class의 인자가 없는 기본 생성자를 필요로 하여 추가함
    constructor() : this(
        "", "", "", PlaceLocation(), "", 0, "", "",
        0, 0, "", 0.0, emptyList(), emptyList(), emptyList(), true
    )
}
