package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.model.remote.Privacy
import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {

    val userDocumentID: Flow<String>

    suspend fun updateUserDocumentId(userDocumentID: String)

    suspend fun removeUserDocumentId()

    suspend fun getRememberId(): String

    suspend fun updateRememberId(id: String)

    suspend fun removeRememberId()

    suspend fun getPrivacyInfo(): Privacy

    suspend fun getLocationPolicyInfo(): Privacy

    suspend fun getDeleteRequestUrl(): String
}