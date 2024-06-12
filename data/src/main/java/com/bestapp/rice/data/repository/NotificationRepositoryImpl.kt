package com.bestapp.rice.data.repository

import com.bestapp.rice.data.di.NetworkProviderModule
import com.bestapp.rice.data.notification.DownloadToken
import com.bestapp.rice.data.notification.PushMsgJson
import com.bestapp.rice.data.notification.setup.KaKaoService
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @NetworkProviderModule.KakaoNotificationRetrofit private val kaKaoService: KaKaoService
) : NotificationRepository {
    override suspend fun registerToken(
        uuid: String,
        deviceId: String,
        pushType: String,
        pushToken: String
    ) {
        kaKaoService.registerToken(uuid = uuid, deviceId = deviceId, pushType = pushType, pushToken = pushToken)
    }

    override suspend fun downloadToken(uuid: String) : DownloadToken {
        return kaKaoService.downloadToken(uuid = uuid)
    }

    override suspend fun deleteToken(uuid: String, deviceId: String, pushType: String) {
        kaKaoService.deleteToken(uuid = uuid, deviceId = deviceId, pushType = pushType)
    }

    override suspend fun sendNotification(
        uuids: List<String>,
        pushMessage: PushMsgJson,
        bypass: Boolean
    ) {
        kaKaoService.sendNotification(uuids = uuids, pushMessage = pushMessage, bypass = false)
    }
}