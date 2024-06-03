package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User

class UserRepositoryImpl: UserRepository {
    override suspend fun getUser(userDocumentId: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun login(id: String, pw: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun signUpUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun signOutUser(userDocumentId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserNickname(userDocumentId: String, newNickname: String) {
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
    }
}