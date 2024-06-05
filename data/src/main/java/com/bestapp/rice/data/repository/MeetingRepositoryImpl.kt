package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Meeting

class MeetingRepositoryImpl: MeetingRepository {
    override suspend fun getMeeting(meetingDocumentID: String): List<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearch(query: String): List<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun getFoodMeeting(mainMenu: String): List<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun getCostMeeting(costType: Int): List<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun createMeeting(mainMenu: String): List<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAttendanceCheckMeeting(meetingDocumentID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun endMeeting(meetingDocumentID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addPendingMember(meetingDocumentID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun approveMember(meetingDocumentID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun rejectMember(meetingDocumentID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getMeetingByUserDocumentID(): List<Meeting> {
        TODO("Not yet implemented")
    }
}