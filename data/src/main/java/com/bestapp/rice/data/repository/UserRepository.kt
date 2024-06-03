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
<<<<<<< HEAD
=======
    suspend fun login(id: String, pw: String): Boolean
    suspend fun signUpUser(user: User)
<<<<<<< HEAD
    suspend fun signOutUser(user: User)
>>>>>>> 22f1f1a (feat: userRepository 및 StorageRepository 구현)
=======
    suspend fun signOutUser(userDocumentId: String)
<<<<<<< HEAD
>>>>>>> 1cf92eb (fix: signOutUser 로직의 오류 수정)
=======
    suspend fun updateUserNickname(userDocumentId: String, nickname: String)
>>>>>>> 1fed898 (feat: 유저 닉네임 변경 기능 추가)
=======
>>>>>>> 65f6a30 (사용자 설정 페이지 구현 (#8))
    suspend fun updateUserTemperature(reviews: List<Review>)
    suspend fun updateUserMeetingCount(userDocumentID: String)
    suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?)
    suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String>
    suspend fun addPost(userID: String, post: Post): Boolean
}