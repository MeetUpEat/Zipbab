package com.bestapp.zipbab.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.bestapp.zipbab.data.FirestoreDB.FirestoreDB
import com.bestapp.zipbab.data.doneSuccessful
import com.bestapp.zipbab.data.model.UploadStateEntity
import com.bestapp.zipbab.data.model.local.SignOutEntity
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.data.model.remote.PostForInit
import com.bestapp.zipbab.data.model.remote.Review
import com.bestapp.zipbab.data.model.remote.SignOutForbiddenResponse
import com.bestapp.zipbab.data.model.remote.UserResponse
import com.bestapp.zipbab.data.notification.fcm.AccessToken
import com.bestapp.zipbab.data.upload.UploadWorker
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val firestoreDB: FirestoreDB,
    private val storageRepository: StorageRepository,
    private val meetingRepository: MeetingRepository,
    private val postRepository: PostRepository,
    @ApplicationContext private val context: Context,
    moshi: Moshi,
) : UserRepository {

    private val workManager = WorkManager.getInstance(context)
    private val jsonAdapter = moshi.adapter(UploadStateEntity::class.java)

    override suspend fun getUser(userDocumentID: String): UserResponse {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("userDocumentID", userDocumentID)
            .get()
            .await()

        for (document in users) {
            return document.toObject<UserResponse>()
        }

        return UserResponse()
    }

    override suspend fun login(id: String, pw: String): String {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("id", id)
            .get()
            .await()

        for (document in users) {
            val userResponse = document.toObject<UserResponse>()
            if (userResponse.pw == pw) {
                return userResponse.userDocumentID
            }
        }
        return ""
    }

    override suspend fun signUpUser(userResponse: UserResponse): String {
        val userDocumentRef = firestoreDB.getUsersDB()
            .add(userResponse)
            .await()
        val userDocumentID = userDocumentRef.id

        firestoreDB.getUsersDB().document(userDocumentID)
            .update("userDocumentID", userDocumentID)
            .doneSuccessful()

        return userDocumentID
    }

    override suspend fun signOutUser(userDocumentID: String): SignOutEntity {
        // 회원탈퇴가 허용되지 않은 아이디인지 확인
        if (checkSignOutIsNotAllowed(userDocumentID)) {
            return SignOutEntity.IsNotAllowed
        }

        // 참여중인 모임 정리하기
        val meetings = meetingRepository.getMeetingByUserDocumentID(userDocumentID) +
                meetingRepository.getPendingMeetingByUserDocumentID(userDocumentID)

        for (meeting in meetings) {
            if (meeting.hostUserDocumentID == userDocumentID) {
                meetingRepository.deleteMeeting(meeting.meetingDocumentID)
            } else {
                meetingRepository.deleteMeetingMember(meeting.meetingDocumentID, userDocumentID)
            }
        }

        // 작성한 포스트 삭제하기
        val posts = postRepository.getPosts(userDocumentID)
        for (post in posts) {
            postRepository.deletePost(userDocumentID, post.postDocumentID)
        }

        // 프로필 이미지 삭제하기
        deleteUserProfileImage(userDocumentID)

        // 회원 탈퇴하기
        val isSuccess = firestoreDB.getUsersDB().document(userDocumentID)
            .delete()
            .doneSuccessful()

        return if (isSuccess) {
            SignOutEntity.Success
        } else {
            SignOutEntity.Fail
        }
    }

    private suspend fun checkSignOutIsNotAllowed(userDocumentID: String): Boolean {
        val documentSnapShot  = firestoreDB.getPolicyDB()
            .document("ForbiddenForDelete")
            .get()
            .await()

        val notAllowedIDs = documentSnapShot.toObject<SignOutForbiddenResponse>()
        return notAllowedIDs?.userDocumentIDs?.contains(userDocumentID) ?: false
    }

    override suspend fun updateUserNickname(userDocumentID: String, nickname: String): Boolean {
        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("nickname", nickname)
            .doneSuccessful()
    }

    override suspend fun updateUserTemperature(reviews: List<Review>): Boolean {
        val isSuccessfuls = Array<Boolean>(reviews.size) { false }

        reviews.forEachIndexed { i, review ->
            if (review.votingPoint == 0.0) {
                isSuccessfuls[i] = true
                return@forEachIndexed
            }

            isSuccessfuls[i] = firestoreDB.getUsersDB().document(review.id)
                .update("temperature", FieldValue.increment(review.votingPoint))
                .doneSuccessful()
        }

        return isSuccessfuls.all { it == true }
    }

    override suspend fun updateUserMeetingCount(userDocumentID: String): Boolean {
        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("meetingCount", FieldValue.increment(1))
            .doneSuccessful()
    }

    override suspend fun updateUserProfileImage(
        userDocumentID: String,
        profileImageUri: String?
    ): Boolean {
        val uri = if (profileImageUri.isNullOrBlank()) {
            ""
        } else {
            storageRepository.uploadImage(
                Uri.parse(profileImageUri)
            )
        }

        // 기존 프로필 이미지 삭제
        deleteUserProfileImage(userDocumentID)

        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("profileImage", uri)
            .doneSuccessful()
    }

    override suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String> {
        val storageUris = Array<String>(images.size) { "" }

        images.forEachIndexed { i, bitmap ->
            storageUris[i] = storageRepository.uploadImage(bitmap)
        }

        return storageUris.toList()
    }

    override suspend fun addPost(userDocumentID: String, images: List<String>): Boolean {
        val imageUrls = mutableListOf<String>()

        for (image in images) {
            val url = storageRepository.uploadImage(
                Uri.parse(image)
            )
            imageUrls.add(url)
        }

        val postDocumentRef = firestoreDB.getPostDB()
            .add(PostForInit(
                images = imageUrls
            ))
            .await()
        val postDocumentId = postDocumentRef.id

        val isSuccess = firestoreDB.getPostDB().document(postDocumentId)
            .update("postDocumentID", postDocumentId)
            .doneSuccessful()

        if (isSuccess.not()) {
            return false
        }

        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("posts", FieldValue.arrayUnion(postDocumentId))
            .doneSuccessful()
    }

    override suspend fun deleteUserProfileImage(userDocumentID: String) {
        val document = firestoreDB.getUsersDB().document(userDocumentID)
            .get()
            .await()
        val userResponse = document.toObject<UserResponse>() ?: return
        storageRepository.deleteImage(userResponse.profileImage)
    }

    override suspend fun addPostWithAsync(
        userDocumentID: String,
        tempPostDocumentID: String,
        images: List<String>
    ): Flow<UploadStateEntity> = flow {
        emit(UploadStateEntity.Pending(tempPostDocumentID))

        val imageUrls = mutableListOf<String>()

        for ((idx, image) in images.withIndex()) {
            emit(
                UploadStateEntity.ProcessImage(
                    tempPostDocumentID,
                    idx + 1,
                    images.size,
                )
            )
            val url = storageRepository.uploadImage(
                Uri.parse(image)
            )
            imageUrls.add(url)
        }

        emit(UploadStateEntity.ProcessPost(tempPostDocumentID))
        val postDocumentRef = firestoreDB.getPostDB()
            .add(
                PostForInit(
                    images = imageUrls
                )
            )
            .await()
        val postDocumentId = postDocumentRef.id

        var isSuccess = firestoreDB.getPostDB().document(postDocumentId)
            .update("postDocumentID", postDocumentId)
            .doneSuccessful()
        if (isSuccess.not()) {
            emit(UploadStateEntity.Fail(tempPostDocumentID))
            // 실패하면 기존 업로드한 이미지 모두 삭제하기
            // TODO : WorkmManager로 넘겨서 다시 시도하도록 수정
            for (url in imageUrls) {
                storageRepository.deleteImage(url)
            }
            return@flow
        }

        isSuccess = firestoreDB.getUsersDB().document(userDocumentID)
            .update("posts", FieldValue.arrayUnion(postDocumentId))
            .doneSuccessful()

        if (isSuccess) {
            emit(UploadStateEntity.SuccessPost(tempPostDocumentID, postDocumentId))
        } else {
            emit(UploadStateEntity.Fail(tempPostDocumentID))
        }
    }

    override fun addPostWithWorkManager(
        workRequestKey: UUID,
        userDocumentID: String,
        tempPostDocumentID: String,
        images: List<String>
    ): Flow<UploadStateEntity> {
        val inputData = Data.Builder()
            .putString(UploadWorker.UPLOAD_USER_DOCUMENT_ID_KEY, userDocumentID)
            .putString(UploadWorker.UPLOAD_TEMP_POST_DOCUMENT_ID_KEY, tempPostDocumentID)
            .putStringArray(UploadWorker.UPLOAD_IMAGES_KEY, images.toTypedArray())
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<UploadWorker>()
            .setId(workRequestKey)
            .setInputData(inputData)
            .setConstraints(constraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        workManager.beginWith(request)
            .enqueue()

        return workManager.getWorkInfoByIdFlow(workRequestKey).map {
            val jsonString = it.progress.getString(UploadWorker.PROGRESS_KEY)
                ?: return@map UploadStateEntity.Pending(tempPostDocumentID)
            jsonAdapter.fromJson(jsonString) ?: UploadStateEntity.Pending(tempPostDocumentID)
        }
    }

    override suspend fun addNotifyListInfo( //cyc noti list갱신 -> meeting쪽에서관리
        userDocumentID: String,
        notificationType: ArrayList<NotificationTypeResponse.UserResponseNotification>
    ) : Boolean {

        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("notificationList", notificationType)
            .doneSuccessful()
    }

    override suspend fun getAccessToken(): AccessToken {
        val querySnapshot = firestoreDB.getAccessDB().document("n9FI6noeU2dFTHbHdQd8")
            .get()
            .await()

        return querySnapshot.toObject<AccessToken>() ?: AccessToken()
    }

    override suspend fun removeItem(udi: String, exchange: ArrayList<NotificationTypeResponse.UserResponseNotification>, index: Int): Boolean {

        return firestoreDB.getUsersDB().document(udi)
            .update("notificationList", exchange)
            .doneSuccessful()
    }
}