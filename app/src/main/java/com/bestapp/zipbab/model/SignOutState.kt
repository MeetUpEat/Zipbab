package com.bestapp.zipbab.model

sealed interface SignOutState {
    data object Success: SignOutState
    data object Fail: SignOutState
    data object IsNotAllowed: SignOutState
}