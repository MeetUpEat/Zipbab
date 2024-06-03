package com.bestapp.rice.data.repository

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

        return querySnapshot.toObject<Privacy>() ?: Privacy()
    }

    override suspend fun getUserInfo(): User {
        TODO()
    }

    override suspend fun removeUserInfo(): Boolean {
        TODO()
    }
}