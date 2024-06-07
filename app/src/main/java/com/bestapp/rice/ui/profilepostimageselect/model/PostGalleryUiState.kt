package com.bestapp.rice.ui.profilepostimageselect.model

import android.net.Uri

data class PostGalleryUiState(
    val uri: Uri,
    val name: String,
    val isSelected: Boolean = false,
    val order: Int = NOT_SELECTED_ORDER,
) {
    companion object {
        const val NOT_SELECTED_ORDER = 0
    }
}
