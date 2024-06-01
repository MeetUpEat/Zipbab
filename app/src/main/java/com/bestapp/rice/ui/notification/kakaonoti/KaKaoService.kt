package com.bestapp.rice.ui.notification.kakaonoti

import com.bestapp.rice.ui.notification.kakaonoti.notientity.DownloadTokenEntity
import com.bestapp.rice.ui.notification.kakaonoti.notientity.PushMsgJson
import com.bestapp.rice.ui.notification.kakaonoti.notientity.RegisterTokenEntity
import com.bestapp.rice.ui.notification.kakaonoti.notientity.SendMsgEntity

interface KaKaoService {

    //@POST("v2/push/register")
    fun registerToken(
        //@Header("Authorization") api_key: String = "어드민 키",
        uuid: String,
        device_id: String,
        push_type: String,
        push_token: String
    ) : RegisterTokenEntity

    //@GET("v2/push/tokens")
    fun downloadToken(
        //@Header("Authorization") api_key: String = "어드민 키",
        uuid: String
    ) : DownloadTokenEntity

    //@POST("v2/push/deregister")
    fun deleteToken(
        //@Header("Authorization") api_key: String = "어드민 키",
        uuid: String,
        device_id: String,
        push_type: String
    )

    //@POST("v2/push/send")
    fun sendNotification(
        //@Header("Authorization") api_key: String = "어드민 키",
        uuids : List<String>,
        push_message : PushMsgJson,
        bypass: Boolean
    ) : SendMsgEntity
}