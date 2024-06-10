package com.bestapp.rice.ui.profilepostimageselect.model

import android.net.Uri

data class PostGalleryUiState(
    val uri: Uri,
    val name: String,
    val order: Int = NOT_SELECTED_ORDER,
) {

    fun isSelected(): Boolean = order != NOT_SELECTED_ORDER

    companion object {
        const val NOT_SELECTED_ORDER = 0
    }
}
