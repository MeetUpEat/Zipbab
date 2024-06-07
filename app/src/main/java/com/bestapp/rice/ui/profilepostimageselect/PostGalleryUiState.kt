package com.bestapp.rice.ui.profilepostimageselect

import android.net.Uri

data class PostGalleryUiState(
    val uri: Uri,
    val name: String,
    val isSelected: Boolean,
    val order: Int,
)
