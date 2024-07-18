package com.bestapp.zipbab.data.repository

import android.util.Log
import com.bestapp.zipbab.data.FirestoreDB.FirestoreDB
import com.bestapp.zipbab.data.doneSuccessful
import com.bestapp.zipbab.data.model.remote.MeetingResponse
import com.bestapp.zipbab.data.model.remote.UserResponse
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
    private val firestoreDB: FirestoreDB,
    private val storageRepository: StorageRepository,
) : MeetingRepository {
    private suspend fun Query.toMeetings(): List<MeetingResponse> {
        val querySnapshot = this.get().await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject<MeetingResponse>()
        }
    }

    override suspend fun getMeeting(meetingDocumentID: String): MeetingResponse {
        val meeting = firestoreDB.getMeetingDB()
            .whereEqualTo("meetingDocumentID", meetingDocumentID)
            .get()
            .await()

        for (document in meeting) {
            return document.toObject<MeetingResponse>()
        }

        return MeetingResponse()
    }

    override suspend fun getMeetings(): List<MeetingResponse> {
        return firestoreDB.getMeetingDB()
            .toMeetings()
    }

    override suspend fun getMeetingByUserDocumentID(userDocumentID: String): List<MeetingResponse> {
        return firestoreDB.getMeetingDB()
            .where(
                Filter.or(
                    Filter.arrayContains("members", userDocumentID),
                    Filter.equalTo("hostUserDocumentID", userDocumentID)
                )
            )
            .toMeetings()
    }

    /**
     * @param keyword 검색어(띄워쓰기 인식 가능)
     */
    override suspend fun getSearch(keyword: String): List<MeetingResponse> {
        val activateMeetings = firestoreDB.getMeetingDB()
            .whereEqualTo("activation", true)
            .toMeetings()

        val querys = keyword.split(" ")

        return activateMeetings.filter { meeting ->
            querys.any { text ->
                text.any {
                    it in meeting.title
                }
            }
        }
    }

    override suspend fun getFoodMeeting(
        mainMenu: String,
        onlyActivation: Boolean
    ): List<MeetingResponse> {
        var query = firestoreDB.getMeetingDB()
            .whereEqualTo("mainMenu", mainMenu)

        if (onlyActivation) {
            query = query.whereEqualTo("activation", true)
        }

        return query.toMeetings()
    }

    override suspend fun getCostMeeting(
        costType: Int,
        onlyActivation: Boolean
    ): List<MeetingResponse> {
        var query = firestoreDB.getMeetingDB()
            .whereEqualTo("costTypeByPerson", costType)

        if (onlyActivation) {
            query = query.whereEqualTo("activation", true)
        }

        return query.toMeetings()
    }

    /**
     * 미팅 추가
     * hostTemperature 가져오기
     * meetingDocumentID 및 hostTemperature 업데이트 로직
     * total : 약 0.5초 소요됨
     */
    override suspend fun createMeeting(meetingResponse: MeetingResponse): Boolean {
        val documentRef = firestoreDB.getMeetingDB()
            .add(meetingResponse)
            .await()

        val meetingDocumentID = documentRef.id
        val hostTemperature = getHostTemperature(meetingResponse.hostUserDocumentID)

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

        val hostUser = querySnapshot.documents.first()
        return hostUser.toObject<UserResponse>()?.temperature ?: Double.MIN_VALUE
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
    override suspend fun addPendingMember(
        meetingDocumentID: String,
        userDocumentID: String
    ): Boolean {
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

    override suspend fun deleteMeeting(meetingDocumentID: String): Boolean {
        val meeting = getMeeting(meetingDocumentID)

        storageRepository.deleteImage(meeting.titleImage)
        return firestoreDB.getMeetingDB().document(meetingDocumentID).delete()
            .doneSuccessful()
    }

    override suspend fun deleteMeetingMember(
        meetingDocumentID: String,
        userDocumentID: String
    ): Boolean {
        val meetingRef = firestoreDB.getMeetingDB().document(meetingDocumentID)
        return firebaseFirestore.runTransaction { transition ->
            transition.update(meetingRef, "pendingMembers", FieldValue.arrayRemove(userDocumentID))
            transition.update(meetingRef, "members", FieldValue.arrayUnion(userDocumentID))
        }.doneSuccessful()
    }

    override suspend fun getPendingMeetingByUserDocumentID(userDocumentID: String): List<MeetingResponse> {
        return firestoreDB.getMeetingDB()
            .where(
                Filter.or(
                    Filter.arrayContains("pendingMembers", userDocumentID),
                )
            )
            .toMeetings()
    }

}
