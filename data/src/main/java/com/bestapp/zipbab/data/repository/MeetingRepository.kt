package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.model.remote.MeetingResponse

interface MeetingRepository {
    suspend fun getMeeting(meetingDocumentID: String): MeetingResponse
    suspend fun getMeetings(): List<MeetingResponse>

    suspend fun getMeetingByUserDocumentID(userDocumentID: String): List<MeetingResponse>

    suspend fun getSearch(keyword: String): List<MeetingResponse>

    suspend fun getFoodMeeting(
        mainMenu: String,
        onlyActivation: Boolean = true
    ): List<MeetingResponse>

    suspend fun getCostMeeting(costType: Int, onlyActivation: Boolean = true): List<MeetingResponse>

    suspend fun createMeeting(meetingResponse: MeetingResponse): Boolean

    suspend fun updateAttendanceCheckMeeting(
        meetingDocumentID: String,
        userDocumentID: String
    ): Boolean

    suspend fun endMeeting(meetingDocumentID: String): Boolean

    // 참여 대기중인 멤버리스트에 신청자 추가하기
    suspend fun addPendingMember(meetingDocumentID: String, userDocumentID: String): Boolean

    // 참여 대기중인 멤버를 참여자 리스트로 옮겨주기
    suspend fun approveMember(meetingDocumentID: String, userDocumentID: String): Boolean

    // pendingmembers 리스트에서 해당 멤버를 제거하기
    suspend fun rejectMember(meetingDocumentID: String, userDocumentID: String): Boolean

    // 탈퇴한 회원이 호스트인 경우 등을 위해 미팅 삭제
    suspend fun deleteMeeting(meetingDocumentID: String): Boolean

    // 탈퇴한 회원이 호스트가 아닌 경우 등을 위해 미팅에서 참여 정보 삭제
    suspend fun deleteMeetingMember(meetingDocumentID: String, userDocumentID: String): Boolean

    // 참여 대기중인 모임 반환
    suspend fun getPendingMeetingByUserDocumentID(userDocumentID: String): List<MeetingResponse>
}