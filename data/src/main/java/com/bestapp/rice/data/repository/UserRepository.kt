package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User


interface UserRepository {
    suspend fun getUser(userDocumentId: String): User
    suspend fun login(): User
    suspend fun signUpUs(user: User)
    suspend fun updateUserTemperature(reviews: List<Review>)
    suspend fun updateUserMeetingCount()
    suspend fun updateUserProfileImage(userID: String, profileImageUri: String?)
    suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String>
    suspend fun addPost(userID: String, post: Post): Boolean
}