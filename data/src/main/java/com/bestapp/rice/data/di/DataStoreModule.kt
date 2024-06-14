package com.bestapp.zipbab.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    @Singleton
    @Provides
    fun providesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zipbab")
}