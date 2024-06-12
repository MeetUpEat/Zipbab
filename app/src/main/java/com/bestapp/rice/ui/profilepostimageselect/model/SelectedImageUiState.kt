package com.bestapp.rice.ui.profilepostimageselect.model

import android.net.Uri

data class SelectedImageUiState(
    val uri: Uri,
    val name: String,
    val order: Int,
)
