package com.bestapp.rice.ui.profilepostimageselect.model

sealed interface SubmitUiState {
    data object Default: SubmitUiState
    data object Uploading: SubmitUiState
    data object Success : SubmitUiState
    data object Fail: SubmitUiState
}