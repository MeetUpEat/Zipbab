package com.bestapp.zipbab.data.model.local

sealed interface SignOutEntity {
    data object Success: SignOutEntity
    data object Fail: SignOutEntity
    data object IsNotAllowed: SignOutEntity
}