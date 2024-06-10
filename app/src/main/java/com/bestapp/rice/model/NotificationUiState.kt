package com.bestapp.rice.model

import com.bestapp.rice.data.model.remote.NotificationType

sealed interface NotificationUiState {
    data class MainNotification( //관리자 전용 알림 데이터 형식
        val dec: String,
        val uploadDate: String
    ) : NotificationUiState

    data class UserNotification( //유저 전용 알림 데이터 형식
        val dec: String,
        val uploadDate: String
    ) : NotificationUiState
}