package com.bestapp.rice.ui.profilepostimageeditfragment.model

import android.net.Uri

data class PostGalleryUiState(
    val uri: Uri,
    val name: String,
    val isSelected: Boolean,
    val order: Int,
)
