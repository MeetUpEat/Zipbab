package com.bestapp.zipbab.ui.setting

sealed interface SettingIntent {
    data object Default : SettingIntent
    data object SignOut : SettingIntent
    data object Logout : SettingIntent
    data object Login : SettingIntent
    data object Profile : SettingIntent
    data object Meeting : SettingIntent
    data object SignUp : SettingIntent
}