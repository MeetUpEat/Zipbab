package com.bestapp.rice.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bestapp.rice.data.FirestorDB.FirestoreDB
import com.bestapp.rice.data.model.remote.Privacy
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

private object PreferencesKeys {
    val USER_DOCUMENT_ID = stringPreferencesKey("user_document_id")
    val USER_ID = stringPreferencesKey("user_id")

}

internal class AppSettingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val firestoreDB : FirestoreDB
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
        val querySnapshot = firestoreDB.getPrivacyDB().document("use")
            .get()
            .await()

        return querySnapshot.toObject<Privacy>() ?: Privacy()
    }

    override suspend fun saveId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
        Log.d("save", id)
    }

    override suspend fun getId(): Flow<String> {
        val result : Flow<String> = dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID] ?: ""
        }
        return result
    }

    override suspend fun removeId() {
        dataStore.edit {
            it.remove(PreferencesKeys.USER_ID)
        }
    }
}