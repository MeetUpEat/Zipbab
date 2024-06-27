package com.bestapp.zipbab.data.model.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface NotificationTypeResponse : Parcelable { // muti-recyclerView 구현을 위한 클래스

    @Parcelize
    data class MainResponseNotification( //관리자 전용 알림 데이터 형식
        val title: String = "",
        val dec: String = "",
        val uploadDate: String = "",
        val meetingDocumentId: String = "",
        val userDocumentId: String = ""
    ) : NotificationTypeResponse, Parcelable

    @Parcelize
    data class UserResponseNotification( //유저 전용 알림 데이터 형식
        val title: String = "",
        val dec: String = "",
        val uploadDate: String = "",
        val meetingDocumentId: String = "",
        val userDocumentId: String = ""
    ) : NotificationTypeResponse, Parcelable
}