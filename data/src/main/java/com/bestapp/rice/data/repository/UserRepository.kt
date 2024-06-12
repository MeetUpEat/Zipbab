package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User


interface UserRepository {
    suspend fun getUser(userDocumentID: String): User
    suspend fun login(id: String, pw: String): Pair<String, Boolean>
    suspend fun signUpUser(user: User): String
    suspend fun signOutUser(userDocumentID: String): Boolean
    suspend fun updateUserNickname(userDocumentID: String, nickname: String): Boolean
    suspend fun updateUserTemperature(reviews: List<Review>): Boolean
    suspend fun updateUserMeetingCount(userDocumentID: String): Boolean
    suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?): Boolean
    suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String>
    suspend fun addPost(userDocumentID: String, images: List<String>): Boolean
}