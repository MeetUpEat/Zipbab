package com.bestapp.zipbab.data.model.remote

sealed interface SignUpResponse {

    data class Success(
        val userDocumentID: String,
    ): SignUpResponse
    data object DuplicateEmail: SignUpResponse
    data object Fail: SignUpResponse
}