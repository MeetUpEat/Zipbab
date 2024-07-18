package com.bestapp.zipbab.ui.signup

sealed interface SignUpState {

    data object Default: SignUpState
    data class Success(
        val userDocumentID: String,
    ): SignUpState
    data object DuplicateEmail: SignUpState
    data object Fail: SignUpState
}