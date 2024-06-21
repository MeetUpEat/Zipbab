package com.bestapp.zipbab.ui.profileimageselect

import android.net.Uri

data class GalleryImageInfo(
    val uri: Uri,
    val name: String,
) {

    companion object {
        fun empty() = GalleryImageInfo(Uri.EMPTY, "")
    }
}