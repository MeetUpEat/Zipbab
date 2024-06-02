package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User


interface UserRepository {
    suspend fun getUser(userDocumentId: String): User
<<<<<<< HEAD
    suspend fun login(): User
    suspend fun signUp(user: User)
    suspend fun signOut(user: User): Boolean
=======
    suspend fun login(id: String, pw: String): Boolean
    suspend fun signUpUser(user: User)
    suspend fun signOutUser(user: User)
>>>>>>> 22f1f1a (feat: userRepository 및 StorageRepository 구현)
    suspend fun updateUserTemperature(reviews: List<Review>)
    suspend fun updateUserMeetingCount()
    suspend fun updateUserProfileImage(userID: String, profileImageUri: String?)
    suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String>
    suspend fun addPost(userID: String, post: Post): Boolean
}