package com.bestapp.zipbab.data.model.remote

sealed interface NotificationTypeResponse { //muti-recyclerView 구현을 위한 클래스

    data class MainResponseNotification( //관리자 전용 알림 데이터 형식
        val dec: String,
        val uploadDate: String
    ) : NotificationTypeResponse {
        constructor() : this("", "")
    }

    data class UserResponseNotification( //유저 전용 알림 데이터 형식
        val dec: String,
        val uploadDate: String
    ) : NotificationTypeResponse {
        constructor() : this("", "")
    }
}