package com.bestapp.zipbab.ui.setting

sealed interface SettingMessage {
    data object Default: SettingMessage
    data object SingOutSuccess: SettingMessage
    data object SignOutFail: SettingMessage
}