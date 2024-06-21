package com.bestapp.zipbab.data.di

import com.bestapp.zipbab.data.repository.StorageRepository
import com.bestapp.zipbab.data.repository.StorageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PersistentRepositoryModule {

    @Binds
    abstract fun bindStorageRepository(storageRepositoryImpl: StorageRepositoryImpl): StorageRepository
}