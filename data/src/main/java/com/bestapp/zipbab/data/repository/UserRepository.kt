package com.bestapp.zipbab.data.repository

import android.graphics.Bitmap
import com.bestapp.zipbab.data.model.remote.Review
import com.bestapp.zipbab.data.model.remote.UserResponse


interface UserRepository {
    suspend fun getUser(userDocumentID: String): UserResponse
    suspend fun login(id: String, pw: String): String
    suspend fun signUpUser(userResponse: UserResponse): String
    suspend fun signOutUser(userDocumentID: String): Boolean
    suspend fun updateUserNickname(userDocumentID: String, nickname: String): Boolean
    suspend fun updateUserTemperature(reviews: List<Review>): Boolean
    suspend fun updateUserMeetingCount(userDocumentID: String): Boolean
    suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?): Boolean
    suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String>
    suspend fun addPost(userDocumentID: String, images: List<String>): Boolean
    suspend fun deleteUserProfileImage(userDocumentID: String)
}