package com.bestapp.zipbab.data.notification.setup

import com.bestapp.zipbab.data.notification.DownloadToken
import com.bestapp.zipbab.data.notification.RegisterToken
import com.bestapp.zipbab.data.notification.SendMsg
import com.bestapp.zipbab.data.notification.SendNotificationRequest
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

    @POST("v2/push/send")
    suspend fun sendNotification(
        @Body request: SendNotificationRequest
    ) : SendMsg
}