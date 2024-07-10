package com.bestapp.zipbab.data.model.remote

sealed interface LoginResponse {

    data class Success(
        val userDocumentID: String
    ) : LoginResponse

    data object Fail : LoginResponse
}
