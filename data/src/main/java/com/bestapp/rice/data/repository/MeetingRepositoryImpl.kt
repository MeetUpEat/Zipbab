package com.bestapp.rice.data.repository

import android.util.Log
import com.bestapp.rice.data.FirestorDB.FirestoreDB
import com.bestapp.rice.data.doneSuccessful
import com.bestapp.rice.data.model.remote.Meeting
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//Hilt 에러 조심(firebaseFirestore 의존성)
internal class MeetingRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firestoreDB : FirestoreDB,
) : MeetingRepository {
    private suspend fun Query.toMeetings(): List<Meeting> {
        val querySnapshot = this.get().await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject<Meeting>()
        }
    }

    override suspend fun getMeeting(meetingDocumentID: String): Meeting {
        val meetings = firestoreDB.getMeetingDB()
            .whereEqualTo("meetingDocumentID", meetingDocumentID)
            .get()
            .await()


        for (document in meetings) {
            return document.toObject<Meeting>()
        }

        return FAKE_MEETING
    }

    override suspend fun getMeetingByUserDocumentID(userDocumentID: String): List<Meeting> {
        return firestoreDB.getMeetingDB()
            .where(Filter.or(
                Filter.arrayContains("members", userDocumentID),
                Filter.equalTo("host", userDocumentID)
            ))
            .toMeetings()
    }

    /**
     * @param query 검색어(띄워쓰기 인식 가능)
     */
    override suspend fun getSearch(query: String): List<Meeting> {
        val activateMeetings = firestoreDB.getMeetingDB()
            .whereEqualTo("activation", true)
            .toMeetings()

        val querys = query.split(" ")

        return activateMeetings.filter { meetings ->
            meetings.title.split(" ").map {
                it in querys
            }.any { it == true }
        }
    }

    override suspend fun getFoodMeeting(mainMenu: String): List<Meeting> {
        return firestoreDB.getMeetingDB()
            .whereEqualTo("mainMenu", mainMenu)
            .toMeetings()
    }

    override suspend fun getCostMeeting(costType: Int): List<Meeting> {
        return firestoreDB.getMeetingDB()
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
        val documentRef = firestoreDB.getMeetingDB()
            .add(meeting)
            .await()

        val meetingDocumentID = documentRef.id
        val hostTemperature = getHostTemperature(meeting.hostUserDocumentID)

        return firebaseFirestore.runTransaction { transition ->
            val meetingRef = firestoreDB.getMeetingDB().document(meetingDocumentID)
            Log.d("새로운 모임 생성", meetingDocumentID)

            transition.update(meetingRef, "meetingDocumentID", meetingDocumentID)
            transition.update(meetingRef, "hostTemperature", hostTemperature)
        }.doneSuccessful()
    }

    private suspend fun getHostTemperature(hostDocumentID: String): Double {
        val querySnapshot = firestoreDB.getUsersDB()
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
        userDocumentID: String,
    ): Boolean {
        return firestoreDB.getMeetingDB().document(meetingDocumentID)
            .update("attendanceCheck", FieldValue.arrayUnion(userDocumentID))
            .doneSuccessful()
    }

    override suspend fun endMeeting(meetingDocumentID: String): Boolean {
        return firestoreDB.getMeetingDB().document(meetingDocumentID)
            .update("activation", false)
            .doneSuccessful()
    }

    /** 참여 대기중인 멤버리스트에 신청자 추가하기 */
    override suspend fun addPendingMember(meetingDocumentID: String, userDocumentID: String): Boolean {
        return firestoreDB.getMeetingDB().document(meetingDocumentID)
            .update("pendingMembers", FieldValue.arrayUnion(userDocumentID))
            .doneSuccessful()
    }

    /** 참여 대기중인 멤버를 참여자 리스트로 옮겨주기 */
    override suspend fun approveMember(meetingDocumentID: String, userDocumentID: String): Boolean {
        return firebaseFirestore.runTransaction { transition ->
            val meetingRef = firestoreDB.getMeetingDB().document(meetingDocumentID)
            transition.update(meetingRef, "pendingMembers", FieldValue.arrayRemove(userDocumentID))
            transition.update(meetingRef, "members", FieldValue.arrayUnion(userDocumentID))
        }.doneSuccessful()
    }

    /** pendingmembers 리스트에서 해당 멤버를 제거하기 */
    override suspend fun rejectMember(meetingDocumentID: String, userDocumentID: String): Boolean {
        return firestoreDB.getMeetingDB().document(meetingDocumentID)
            .update("pendingMembers", FieldValue.arrayRemove(userDocumentID))
            .doneSuccessful()
    }

    companion object {
        private val FAKE_MEETING = Meeting()
    }
}
