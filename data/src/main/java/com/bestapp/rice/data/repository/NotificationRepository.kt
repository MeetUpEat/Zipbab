package com.bestapp.rice.data.repository

import com.bestapp.rice.data.notification.DownloadToken
import com.bestapp.rice.data.notification.SendNotificationRequest

interface NotificationRepository {

    suspend fun registerToken(uuid: String, deviceId: String, pushType: String, pushToken: String)

    suspend fun downloadToken(uuid: String) : DownloadToken

    suspend fun deleteToken(uuid: String, deviceId: String, pushType: String)

    suspend fun sendNotification(sendInfo: SendNotificationRequest)

}