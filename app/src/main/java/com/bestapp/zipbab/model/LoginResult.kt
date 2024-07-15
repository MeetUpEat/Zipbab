package com.bestapp.zipbab.model

sealed interface LoginResult {

    data class Success(
        val userDocumentID: String
    ) : LoginResult

    data object Fail : LoginResult
}