package com.bestapp.rice.data.repository

import android.graphics.Bitmap
<<<<<<< HEAD
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User

class UserRepositoryImpl : UserRepository {
    override suspend fun getUser(userDocumentId: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun login(): User {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun signOut(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserTemperature(reviews: List<Review>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserMeetingCount() {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserProfileImage(userID: String, profileImageUri: String?) {
        TODO("Not yet implemented")
    }

    override suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun addPost(userID: String, post: Post): Boolean {
        TODO("Not yet implemented")
=======
import android.net.Uri
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.data.network.FirebaseClient
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.net.URI

class UserRepositoryImpl : UserRepository {
    private val userDB = FirebaseClient.userStoreService

    // TODO: DataStore의 userDocumentId로 변경 후, 파라미터 제거해야함
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
            .update("userDocumentId", userDocumentId).await()
    }

    override suspend fun signOutUser(user: User) {
        val userDocumentRef = userDB.add(user).await()
        val userDocumentId = userDocumentRef.id

        userDB.document(userDocumentId)
            .delete()
            .await()
    }

    override suspend fun updateUserTemperature(reviews: List<Review>) {
        reviews.forEach { (id, votingPoint) ->
            userDB.document(id)
                .update(
                    "temperature",
                    FieldValue.increment(votingPoint)
                )
        }
    }

    override suspend fun updateUserMeetingCount() {
        // TODO: DataStore에서 id값 가져와서 넣도록 수정해야함
        userDB.document("DataStore id값")
            .update(
                "meetingCount",
                FieldValue.increment(1)
            )
    }

    override suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?) {
        // TODO: null일 때, 로직 수정 필요
        val uri = storageRepositoryImpl.uploadImage(
            Uri.parse(profileImageUri ?: FAKE_USER.profileImage)
        )

        userDB.document(userDocumentID)
            .update(
                "profileImage",
                uri
            )
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

        private val FAKE_USER = User(
            userDocumentID = "neglegentur",
            nickName = "Will Nash",
            id = "hinc",
            pw = "liber",
            profileImage = "https://firebasestorage.googleapis.com/v0/b/food-879fc.appspot.com/o/images%2F1717358827904.jpg?alt=media&token=9587a475-0e22-4719-9680-a9cd66b31ea3",
            temperature = 40.0,
            meetingCount = 1155,
            posts = listOf(),
            placeLocation = PlaceLocation(
                locationAddress = "epicuri",
                locationLat = "lacinia",
                locationLong = "libris"
            )
        )
>>>>>>> 22f1f1a (feat: userRepository 및 StorageRepository 구현)
    }
}