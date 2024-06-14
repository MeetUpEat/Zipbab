package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.MeetingResponse

interface MeetingRepository {
    suspend fun getMeeting(meetingDocumentID: String): MeetingResponse
    suspend fun getMeetings(): List<MeetingResponse>

    suspend fun getMeetingByUserDocumentID(userDocumentID: String): List<MeetingResponse>

    suspend fun getSearch(query: String): List<MeetingResponse>

    suspend fun getFoodMeeting(mainMenu: String): List<MeetingResponse>

    suspend fun getCostMeeting(costType: Int): List<MeetingResponse>

    suspend fun createMeeting(meetingResponse: MeetingResponse): Boolean

    suspend fun updateAttendanceCheckMeeting(meetingDocumentID: String, userDocumentID: String): Boolean

    suspend fun endMeeting(meetingDocumentID: String): Boolean

    // 참여 대기중인 멤버리스트에 신청자 추가하기
    suspend fun addPendingMember(meetingDocumentID: String, userDocumentID: String): Boolean

    // 참여 대기중인 멤버를 참여자 리스트로 옮겨주기
    suspend fun approveMember(meetingDocumentID: String, userDocumentID: String): Boolean

    // pendingmembers 리스트에서 해당 멤버를 제거하기
    suspend fun rejectMember(meetingDocumentID: String, userDocumentID: String): Boolean
}