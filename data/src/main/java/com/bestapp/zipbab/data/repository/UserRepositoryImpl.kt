package com.bestapp.zipbab.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.bestapp.zipbab.data.FirestorDB.FirestoreDB
import com.bestapp.zipbab.data.doneSuccessful
import com.bestapp.zipbab.data.model.remote.NotificationType
import com.bestapp.zipbab.data.model.remote.PostForInit
import com.bestapp.zipbab.data.model.remote.Review
import com.bestapp.zipbab.data.model.remote.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val firestoreDB: FirestoreDB,
    private val storageRepository: StorageRepository,
    private val meetingRepository: MeetingRepository,
    private val postRepository: PostRepository,
) : UserRepository {

    override suspend fun getUser(userDocumentID: String): User {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("userDocumentID", userDocumentID)
            .get()
            .await()

        for (document in users) {
            return document.toObject<User>()
        }

        return User()
    }

    override suspend fun login(id: String, pw: String): String {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("id", id)
            .get()
            .await()

        for (document in users) {
            val user = document.toObject<User>()
            if (user.pw == pw) {
                return user.userDocumentID
            }
        }
        return ""
    }

    override suspend fun signUpUser(user: User): String {
        val userDocumentRef = firestoreDB.getUsersDB()
            .add(user)
            .await()
        val userDocumentID = userDocumentRef.id

        firestoreDB.getUsersDB().document(userDocumentID)
            .update("userDocumentID", userDocumentID)
            .doneSuccessful()

        return userDocumentID
    }

    override suspend fun signOutUser(userDocumentID: String): Boolean {
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
        return firestoreDB.getUsersDB().document(userDocumentID)
            .delete()
            .doneSuccessful()
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
        val user = document.toObject<User>() ?: return
        storageRepository.deleteImage(user.profileImage)
    }

    override suspend fun addNotifyListInfo(
        userDocumentID: String,
        notificationType: NotificationType
    ) : Boolean {
        val result = getUser(userDocumentID)
        var list = result.notificationList
        list += notificationType

        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("notificationList", list)
            .doneSuccessful()
    }
}