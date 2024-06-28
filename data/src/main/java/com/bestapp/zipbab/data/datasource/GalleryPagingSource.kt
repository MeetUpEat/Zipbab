package com.bestapp.zipbab.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bestapp.zipbab.data.model.local.GalleryImageInfo
import kotlin.math.max

class GalleryPagingSource(
    private val galleryImageFetcher: GalleryImageFetcher,
) : PagingSource<Int, GalleryImageInfo>() {
    override fun getRefreshKey(state: PagingState<Int, GalleryImageInfo>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val galleryImageInfo = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = galleryImageInfo.orderId - (state.config.pageSize / 2))
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImageInfo> {
        val start = params.key ?: STARTING_KEY
        val range = start.until(start + params.loadSize)

        val data = galleryImageFetcher.getImageFromGallery(range)
        val nextKey = if (data.isEmpty()) {
            null
        } else {
            range.last + 1
        }

        return LoadResult.Page(
            data = data,
            prevKey = when (start) {
                STARTING_KEY -> null
                else -> ensureValidKey(key = range.first - params.loadSize)
            },
            nextKey = nextKey,
        )
    }

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    companion object {
        private const val STARTING_KEY = 0
    }
}