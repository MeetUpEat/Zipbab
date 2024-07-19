package com.bestapp.zipbab.ui.setting

/**
 * Compose 기반의 Navigation Component로 migration 하기 전까지, Compose에서 Navigation Action을 호출하기 위한 용도로 사용
 */
sealed interface NavActionIntent {
    data object Default : NavActionIntent
    data object Login : NavActionIntent
    data object SignUp: NavActionIntent
    data object Meeting : NavActionIntent
    data class Profile(
        val userDocumentID: String,
    ) : NavActionIntent
    data object Alert : NavActionIntent
}