package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Privacy
import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {

    val userPreferencesFlow: Flow<String>

    suspend fun updateUserDocumentId(userDocumentID: String)

    suspend fun removeUserDocumentId()

    suspend fun getPrivacyInfo(): Privacy

    suspend fun saveId(id: String)

    suspend fun getId() : Flow<String>

    suspend fun removeId()

    suspend fun saveDocument(document: String)

    suspend fun getDeleteRequestUrl(): String
}