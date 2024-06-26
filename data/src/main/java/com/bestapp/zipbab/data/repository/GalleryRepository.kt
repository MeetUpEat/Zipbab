package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.datasource.GalleryPagingSource

interface GalleryRepository {

    fun galleryPagingSource(): GalleryPagingSource
}