package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.bestapp.rice.data.doneSuccessful
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

    override suspend fun signUpUser(user: User): Boolean {
        val userDocumentRef = userDB
            .add(user)
            .await()
        val userDocumentId = userDocumentRef.id

        return userDB.document(userDocumentId)
            .update("userDocumentId", userDocumentId)
            .doneSuccessful()
    }

    override suspend fun signOutUser(userDocumentId: String): Boolean {
        return userDB.document(userDocumentId)
            .delete()
            .doneSuccessful()
    }

    override suspend fun updateUserNickname(userDocumentId: String, nickname: String): Boolean {
        return userDB.document(userDocumentId)
            .update("nickname", nickname)
            .doneSuccessful()
    }

    override suspend fun updateUserTemperature(reviews: List<Review>): Boolean {
        val isSuccessfuls = Array<Boolean>(reviews.size) { false }

        reviews.forEachIndexed { i, review ->
            isSuccessfuls[i] = userDB.document(review.id)
                .update("temperature", FieldValue.increment(review.votingPoint))
                .doneSuccessful()
        }

        return isSuccessfuls.all { it == true }
    }

    override suspend fun updateUserMeetingCount(userDocumentID: String): Boolean {
        return userDB.document(userDocumentID)
            .update("meetingCount", FieldValue.increment(1))
            .doneSuccessful()
    }

    override suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?): Boolean {
        val uri = if (profileImageUri.isNullOrBlank()) {
            USER_DEFAULT_IMAGE_URI
        } else {
            storageRepositoryImpl.uploadImage(
                Uri.parse(profileImageUri)
            )
        }

        return userDB.document(userDocumentID)
            .update("profileImage", uri)
            .doneSuccessful()
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
            .update("posts", FieldValue.arrayUnion(post))
            .doneSuccessful()
    }

    companion object {
        private val storageRepositoryImpl = StorageRepositoryImpl()

        private val FAKE_USER = User()
        private val USER_DEFAULT_IMAGE_URI = "https://firebasestorage.googleapis.com/v0/b/food-879fc.appspot.com/o/images%2F1717515927323.jpg?alt=media&token=026118a6-50ff-4add-a371-5d7f7feda46c"
    }
}