package com.bestapp.rice.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bestapp.rice.data.model.remote.Privacy
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private object PreferencesKeys {
    val USER_DOCUMENT_ID = stringPreferencesKey("user_document_id")
    val USER_ID = stringPreferencesKey("user_id")
}

class AppSettingRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val privacyDB : CollectionReference,
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

    override suspend fun getPrivacyInfo(): Privacy {
        val querySnapshot = privacyDB.document("use")
            .get()
            .await()

        return querySnapshot.toObject<Privacy>() ?: Privacy()
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