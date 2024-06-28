package com.bestapp.zipbab.data.di

import android.content.Context
import com.bestapp.zipbab.data.datasource.GalleryImageFetcher
import com.bestapp.zipbab.data.datasource.GalleryPagingSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityRetainedComponent::class)
class GalleryModule {

    @Provides
    fun provideGalleryPagingSource(galleryImageFetcher: GalleryImageFetcher): GalleryPagingSource {
        return GalleryPagingSource(galleryImageFetcher)
    }

    @Provides
    fun provideGalleryImageFetcher(@ApplicationContext context: Context): GalleryImageFetcher {
        return GalleryImageFetcher(context.contentResolver)
    }
}