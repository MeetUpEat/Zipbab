package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.User

class AppSettingRepositoryImpl : AppSettingRepository {
    override suspend fun getPrivacyInfo(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(): User {
        TODO("Not yet implemented")
    }
}