package com.bestapp.zipbab.ui.setting

sealed interface ActionIntent {

    data object Default: ActionIntent
    data class DirectToRequestDelete(
        val url: String,
    ) : ActionIntent
}