package com.bestapp.rice.data.repository

import kotlinx.coroutines.flow.Flow
import com.bestapp.rice.data.model.remote.Privacy
import com.bestapp.rice.data.model.remote.User

interface AppSettingRepository {

    val userPreferencesFlow: Flow<String>

    suspend fun updateUserDocumentId(userDocumentId: String)

    suspend fun removeUserDocumentId()
    suspend fun getPrivacyInfo(): Privacy

    suspend fun saveId(id: String)
    suspend fun removeId()
}