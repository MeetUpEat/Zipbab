package com.bestapp.rice.data.di

import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.AppSettingRepositoryImpl
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.CategoryRepositoryImpl
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.MeetingRepositoryImpl
import com.bestapp.rice.data.repository.PostRepository
import com.bestapp.rice.data.repository.PostRepositoryImpl
import com.bestapp.rice.data.repository.ReportRepository
import com.bestapp.rice.data.repository.ReportRepositoryImpl
import com.bestapp.rice.data.repository.SearchLocationRepository
import com.bestapp.rice.data.repository.SearchLocationRepositoryImpl
import com.bestapp.rice.data.repository.StorageRepository
import com.bestapp.rice.data.repository.StorageRepositoryImpl
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppSettingRepository(appSettingRepositoryImpl: AppSettingRepositoryImpl): AppSettingRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindMeetingRepository(meetingRepositoryImpl: MeetingRepositoryImpl): MeetingRepository

    @Binds
    @Singleton
    abstract fun bindSearchLocationRepository(searchLocationRepositoryImpl: SearchLocationRepositoryImpl): SearchLocationRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(storageRepositoryImpl: StorageRepositoryImpl): StorageRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository

}