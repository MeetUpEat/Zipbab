package com.bestapp.rice.data.notification.setup

import com.bestapp.rice.data.notification.DownloadToken
import com.bestapp.rice.data.notification.PushMsgJson
import com.bestapp.rice.data.notification.RegisterToken
import com.bestapp.rice.data.notification.SendMsg
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KaKaoService {
    @FormUrlEncoded
    @POST("v2/push/register")
    suspend fun registerToken(
        @Field("uuid") uuid: String,
        @Field("device_id") deviceId: String,
        @Field("push_type") pushType: String,
        @Field("push_token") pushToken: String
    ) : RegisterToken

    @GET("v2/push/tokens")
    suspend fun downloadToken(
        @Query("uuid") uuid: String
    ) : DownloadToken

    @FormUrlEncoded
    @POST("v2/push/deregister")
    suspend fun deleteToken(
        @Field("uuid") uuid: String,
        @Field("device_id") deviceId: String,
        @Field("push_type") pushType: String
    )

    @FormUrlEncoded
    @POST("v2/push/send")
    suspend fun sendNotification(
        @Field("uuids") uuids : List<String>,
        @Body pushMessage : PushMsgJson,
        @Field("bypass") bypass: Boolean
    ) : SendMsg
}