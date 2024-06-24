package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.datasource.GalleryImageFetcher
import com.bestapp.zipbab.data.datasource.GalleryPagingSource
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    private val galleryImageFetcher: GalleryImageFetcher
): GalleryRepository {
    override fun galleryPagingSource(): GalleryPagingSource {
        return GalleryPagingSource(galleryImageFetcher)
    }
}