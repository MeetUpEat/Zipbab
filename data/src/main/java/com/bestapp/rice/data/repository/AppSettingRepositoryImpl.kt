package com.bestapp.rice.data.repository

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 65f6a30 (사용자 설정 페이지 구현 (#8))
import com.bestapp.rice.data.model.remote.User

class AppSettingRepositoryImpl : AppSettingRepository {
    override suspend fun getPrivacyInfo(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(): User {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserInfo(): Boolean {
        TODO("Not yet implemented")
<<<<<<< HEAD
=======
import com.bestapp.rice.data.model.remote.Privacy
import com.bestapp.rice.data.model.remote.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class AppSettingRepositoryImpl(
    val privacyDB : CollectionReference
): AppSettingRepository {
    override suspend fun getPrivacyInfo(): Privacy {
        val querySnapshot = privacyDB.document("use")
            .get()
            .await()

        val data = querySnapshot.toObject<Privacy>()

        return data ?: Privacy()
    }

    override suspend fun getUserInfo(): User {
        TODO()
>>>>>>> 90d0c73 (feat: Privacy get method)
=======
>>>>>>> 65f6a30 (사용자 설정 페이지 구현 (#8))
    }
}