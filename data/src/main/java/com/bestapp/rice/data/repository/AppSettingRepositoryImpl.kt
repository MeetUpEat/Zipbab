package com.bestapp.rice.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private object PreferencesKeys {
    val USER_DOCUMENT_ID = stringPreferencesKey("user_document_id")
    val USER_ID = stringPreferencesKey("user_id")
}

class AppSettingRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
): AppSettingRepository {

    override val userPreferencesFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_DOCUMENT_ID] ?: ""
        }

    override suspend fun updateUserDocumentId(userDocumentId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_DOCUMENT_ID] = userDocumentId
        }
    }

    override suspend fun removeUserDocumentId() {
        dataStore.edit {
            it.remove(PreferencesKeys.USER_DOCUMENT_ID)
        }
    }

    override suspend fun getPrivacyInfo(): String {
        TODO() // FireStore로부터 노션 링크를 가져와야 한다.
    }

    override suspend fun saveId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
    }

    override suspend fun removeId() {
        dataStore.edit {
            it.remove(PreferencesKeys.USER_ID)
        }
    }
}