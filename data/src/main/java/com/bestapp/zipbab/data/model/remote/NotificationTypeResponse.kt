package com.bestapp.zipbab.data.model.remote

data class NotificationTypeResponse(
    val type: NotificationType = NotificationType.REGISTER_MEETING,
    val uploadDate: String = "",
    val meetingDocumentId: String = "",
    val userDocumentId: String = ""
)