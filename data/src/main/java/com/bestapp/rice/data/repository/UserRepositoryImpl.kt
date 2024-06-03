package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val userDB: CollectionReference,
) : UserRepository {

    override suspend fun getUser(userDocumentId: String): User {
        val users = userDB
            .whereEqualTo("userDocumentID", userDocumentId)
            .get()
            .await()

        for (document in users) {
            return document.toObject<User>()
        }

        return FAKE_USER
    }

    override suspend fun login(id: String, pw: String): Boolean {
        val users = userDB
            .whereEqualTo("id", id)
            .get()
            .await()

        for (document in users) {
            val user = document.toObject<User>()
            return user.pw == pw
        }

        return false
    }

    override suspend fun signUpUser(user: User) {
        val userDocumentRef = userDB.add(user).await()
        val userDocumentId = userDocumentRef.id

        userDB.document(userDocumentId)
            .update("userDocumentId", userDocumentId)
            .await()
    }

    override suspend fun signOutUser(userDocumentId: String) {
        userDB.document(userDocumentId)
            .delete()
            .await()
    }

    override suspend fun updateUserNickname(userDocumentId: String, nickname: String) {
        userDB.document(userDocumentId)
            .update("nickname", nickname)
            .await()
    }
    override suspend fun updateUserTemperature(reviews: List<Review>) {
        reviews.forEach { (id, votingPoint) ->
            userDB.document(id)
                .update("temperature", FieldValue.increment(votingPoint))
                .await()
        }
    }

    override suspend fun updateUserMeetingCount(userDocumentID: String) {
        userDB.document(userDocumentID)
            .update("meetingCount", FieldValue.increment(1))
            .await()
    }

    override suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?) {
        // TODO: null일 때, 로직 수정 필요
        val uri = storageRepositoryImpl.uploadImage(
            Uri.parse(profileImageUri ?: FAKE_USER.profileImage)
        )

        userDB.document(userDocumentID)
            .update("profileImage", uri)
            .await()
    }

    override suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String> {
        val storageUris = Array<String>(images.size) { "" }

        images.forEachIndexed { i, bitmap ->
            storageUris[i] = storageRepositoryImpl.uploadImage(bitmap)
        }

        return storageUris.toList()
    }

    override suspend fun addPost(userDocumentID: String, post: Post): Boolean {
        return userDB.document(userDocumentID)
            .update(
                "posts",
                FieldValue.arrayUnion(post)
            ).isSuccessful
    }

    companion object {
        private val storageRepositoryImpl = StorageRepositoryImpl()

        private val FAKE_USER = User()
    }
}