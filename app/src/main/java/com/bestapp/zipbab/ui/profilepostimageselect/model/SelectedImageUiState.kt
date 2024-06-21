package com.bestapp.zipbab.ui.profilepostimageselect.model

import android.net.Uri

data class SelectedImageUiState(
    val uri: Uri,
    val name: String,
    val order: Int,
) {

    companion object {
        private const val NOT_SELECTED_ORDER = -1

        fun empty() = SelectedImageUiState(Uri.EMPTY, "", NOT_SELECTED_ORDER)
    }
}