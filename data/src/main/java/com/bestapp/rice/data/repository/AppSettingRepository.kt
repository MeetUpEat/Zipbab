package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Privacy
import com.bestapp.rice.data.model.remote.User

interface AppSettingRepository {
    suspend fun getPrivacyInfo(): Privacy

    suspend fun getUserInfo(): User

    suspend fun removeUserInfo(): Boolean
}