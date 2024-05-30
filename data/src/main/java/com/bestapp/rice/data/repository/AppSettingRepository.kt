package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.User

interface AppSettingRepository {
    suspend fun getPrivacyInfo(): String

    suspend fun getUserInfo(): User
}