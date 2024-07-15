package com.bestapp.zipbab.ui.login

sealed interface LoginState {

    data object Default: LoginState
    data object Success: LoginState
    data object Fail: LoginState
}