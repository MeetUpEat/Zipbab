package com.bestapp.rice.data.repository

import com.bestapp.rice.data.datastore.UserPreferences
import com.bestapp.rice.data.model.remote.User
import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {

    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun getPrivacyInfo(): String

    suspend fun updateUserInfo(user: User)
    suspend fun removeUserInfo(): Boolean
}