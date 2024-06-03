package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Meeting
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class MeetingRepositoryImpl(
    private val store: FirebaseFirestore,
    private val meetingDB: CollectionReference,
) : MeetingRepository {
    private suspend fun Query.toMeetings(): List<Meeting> {
        val querySnapshot = this.get().await()

        val meetings = ArrayList<Meeting>(querySnapshot.documents.size)

        querySnapshot.forEachIndexed { i, document ->
            meetings[i] = document.toObject<Meeting>()
        }

        return meetings.toList()
    }

    override suspend fun getMeeting(meetingDocumentID: String): List<Meeting> {
        return meetingDB
            .whereEqualTo("meetingDocumentID", meetingDocumentID)
            .toMeetings()
    }

    /**
     * @param query 검색어(띄워쓰기 인식 가능)
     */
    override suspend fun getSearch(query: String): List<Meeting> {
        return meetingDB
            .whereArrayContains("title", query.split(" "))
            .toMeetings()
    }

    override suspend fun getFoodMeeting(mainMenu: String): List<Meeting> {
        return meetingDB
            .whereEqualTo("mainMenu", mainMenu)
            .toMeetings()
    }

    override suspend fun getCostMeeting(costType: Int): List<Meeting> {
        return meetingDB
            .whereEqualTo("costTypeByPerson", costType)
            .toMeetings()
    }

    /**
     * 미팅 추가 및 meetingDocumentID 업데이트 로직 추가
     */
    override suspend fun createMeeting(meeting: Meeting) {
        val documentRef = meetingDB
            .add(meeting)
            .await()

        val meetingDocumentID = documentRef.id

        meetingDB.document(meetingDocumentID)
            .update("meetingDocumentID", meetingDocumentID)
            .await()
    }

    // TODO: DataStore의 userDocumentId 적용 필요
    override suspend fun updateAttendanceCheckMeeting(
        meetingDocumentID: String,
        userDocumentId: String,
    ) {
        meetingDB.document(meetingDocumentID)
            .update("attendanceCheck", FieldValue.arrayUnion(userDocumentId))
            .await()
    }

    override suspend fun endMeeting(meetingDocumentID: String) {
        meetingDB.document(meetingDocumentID)
            .update("activation", false)
            .await()
    }

    /** 참여 대기중인 멤버리스트에 신청자 추가하기 */
    // TODO: DataStore의 userDocumentId 적용 필요
    override suspend fun addPendingMember(meetingDocumentID: String, userDocumentId: String) {
        meetingDB.document(meetingDocumentID)
            .update("pendingMembers", FieldValue.arrayUnion(userDocumentId))
            .await()
    }

    /** 참여 대기중인 멤버를 참여자 리스트로 옮겨주기 */
    // TODO: DataStore의 userDocumentId 적용 필요
    override suspend fun approveMember(meetingDocumentID: String, userDocumentId: String) {
        store.runTransaction { transition ->
            val meetingRef = meetingDB.document(meetingDocumentID)
            transition.update(meetingRef, "pendingMembers", FieldValue.arrayRemove(userDocumentId))
            transition.update(meetingRef, "members", FieldValue.arrayUnion(userDocumentId))
        }.await()
    }

    /** pendingmembers 리스트에서 해당 멤버를 제거하기 */
    // TODO: DataStore의 userDocumentId 적용 필요
    override suspend fun rejectMember(meetingDocumentID: String, userDocumentId: String) {
        meetingDB.document(meetingDocumentID)
            .update("pendingMembers", FieldValue.arrayRemove(userDocumentId))
            .await()
    }
}