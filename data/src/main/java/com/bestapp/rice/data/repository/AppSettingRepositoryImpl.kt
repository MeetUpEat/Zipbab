package com.bestapp.rice.data.repository

<<<<<<< HEAD
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
    }
}