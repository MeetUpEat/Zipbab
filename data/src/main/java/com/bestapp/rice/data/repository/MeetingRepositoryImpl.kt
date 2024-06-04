package com.bestapp.rice.data.repository

import android.util.Log
import com.bestapp.rice.data.doneSuccessful
import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.network.FirebaseClient
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

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject<Meeting>()
        }
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
     * 미팅 추가
     * hostTemperature 가져오기
     * meetingDocumentID 및 hostTemperature 업데이트 로직
     * total : 약 0.5초 소요됨
     */
    override suspend fun createMeeting(meeting: Meeting): Boolean {
        val documentRef = meetingDB
            .add(meeting)
            .await()

        val meetingDocumentID = documentRef.id
        val hostTemperature = getHostTemperature(meeting.host)

        return store.runTransaction { transition ->
            val meetingRef = meetingDB.document(meetingDocumentID)
            Log.d("새로운 모임 생성", meetingDocumentID)

            transition.update(meetingRef, "meetingDocumentID", meetingDocumentID)
            transition.update(meetingRef, "hostTemperature", hostTemperature)
        }.doneSuccessful()
    }

    private suspend fun getHostTemperature(hostDocumentID: String): Double {
        val querySnapshot = FirebaseClient.store.collection("users")
            .whereEqualTo("userDocumentID", hostDocumentID)
            .get()
            .await()

        querySnapshot.documents.mapNotNull { document ->
            document.toObject<Meeting>()?.hostTemperature
        }

        return Double.MIN_VALUE
    }

    override suspend fun updateAttendanceCheckMeeting(
        meetingDocumentID: String,
        userDocumentId: String,
    ): Boolean {
        return meetingDB.document(meetingDocumentID)
            .update("attendanceCheck", FieldValue.arrayUnion(userDocumentId))
            .doneSuccessful()
    }

    override suspend fun endMeeting(meetingDocumentID: String): Boolean {
        return meetingDB.document(meetingDocumentID)
            .update("activation", false)
            .doneSuccessful()
    }

    /** 참여 대기중인 멤버리스트에 신청자 추가하기 */
    override suspend fun addPendingMember(meetingDocumentID: String, userDocumentId: String): Boolean {
        return meetingDB.document(meetingDocumentID)
            .update("pendingMembers", FieldValue.arrayUnion(userDocumentId))
            .doneSuccessful()
    }

    /** 참여 대기중인 멤버를 참여자 리스트로 옮겨주기 */
    override suspend fun approveMember(meetingDocumentID: String, userDocumentId: String): Boolean {
        return store.runTransaction { transition ->
            val meetingRef = meetingDB.document(meetingDocumentID)
            transition.update(meetingRef, "pendingMembers", FieldValue.arrayRemove(userDocumentId))
            transition.update(meetingRef, "members", FieldValue.arrayUnion(userDocumentId))
        }.doneSuccessful()
    }

    /** pendingmembers 리스트에서 해당 멤버를 제거하기 */
    override suspend fun rejectMember(meetingDocumentID: String, userDocumentId: String): Boolean {
        return meetingDB.document(meetingDocumentID)
            .update("pendingMembers", FieldValue.arrayRemove(userDocumentId))
            .doneSuccessful()
    }
}
