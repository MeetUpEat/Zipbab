package com.bestapp.rice.ui.profileedit

sealed interface SubmitUiState {
    data object Uploading: SubmitUiState
    data object Success : SubmitUiState
    data object SubmitNicknameFail : SubmitUiState
    data object SubmitProfileFail : SubmitUiState
}