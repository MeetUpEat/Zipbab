package com.bestapp.zipbab.data.model.local

import android.net.Uri

data class GalleryImageInfo(
    val uri: Uri,
    val name: String,
    val orderId: Int,
) {

    companion object {
        fun empty() = GalleryImageInfo(Uri.EMPTY, "", -1)
    }
}