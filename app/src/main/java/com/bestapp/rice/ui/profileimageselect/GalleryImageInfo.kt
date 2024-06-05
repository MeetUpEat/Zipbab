package com.bestapp.rice.ui.profileimageselect

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryImageInfo(
    val uri: Uri,
    val name: String,
): Parcelable {
    fun isEmpty(): Boolean = this == Empty

    companion object {
        val Empty = GalleryImageInfo(
            uri = Uri.EMPTY,
            name = "",
        )
    }
}
