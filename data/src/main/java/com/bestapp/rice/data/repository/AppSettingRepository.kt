package com.bestapp.rice.data.repository

import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {

    val userPreferencesFlow: Flow<String>

    suspend fun updateUserDocumentId(userDocumentId: String)

    suspend fun removeUserDocumentId()
    suspend fun getPrivacyInfo(): String

    suspend fun saveId(id: String)
    suspend fun removeId()
}