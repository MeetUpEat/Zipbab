package com.bestapp.zipbab.ui.mettinginfo

sealed interface RegisterState {
    data object NotYet : RegisterState
    data object Joined : RegisterState
    data object Requested : RegisterState
}