package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.model.remote.Privacy
import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {

    val userPreferencesFlow: Flow<String>

    suspend fun updateUserDocumentId(userDocumentID: String)

    suspend fun removeUserDocumentId()

    suspend fun getPrivacyInfo(): Privacy

    suspend fun saveId(id: String)

    suspend fun getId() : Flow<String>

    suspend fun removeId()

    suspend fun getDeleteRequestUrl(): String
}