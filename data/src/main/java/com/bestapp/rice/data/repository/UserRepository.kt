package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User


interface UserRepository {
    suspend fun getUser(userDocumentId: String): User
    suspend fun login(id: String, pw: String): Boolean
    suspend fun signUpUser(user: User)
    suspend fun signOutUser(userDocumentId: String)
    suspend fun updateUserNickname(userDocumentId: String, nickname: String)
    suspend fun updateUserTemperature(reviews: List<Review>)
    suspend fun updateUserMeetingCount(userDocumentID: String)
    suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?)
    suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String>
    suspend fun addPost(userID: String, post: Post): Boolean
}