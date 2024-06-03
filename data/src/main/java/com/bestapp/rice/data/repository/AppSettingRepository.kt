package com.bestapp.rice.data.repository

import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {

    val userPreferencesFlow: Flow<String>

    suspend fun getPrivacyInfo(): String

    suspend fun updateUserDocumentId(userDocumentId: String)
    suspend fun removeUserDocumentId()
}