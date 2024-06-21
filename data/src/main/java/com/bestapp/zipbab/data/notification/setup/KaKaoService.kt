package com.bestapp.zipbab.data.notification.setup

import com.bestapp.zipbab.data.BuildConfig
import com.bestapp.zipbab.data.notification.DownloadToken
import com.bestapp.zipbab.data.notification.RegisterToken
import com.bestapp.zipbab.data.notification.fcm.PushNotification
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
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

    @POST("v1/projects/food-879fc/messages:send")
    suspend fun sendNotification(
        @Header("Authorization") apikey: String = "Bearer ${BuildConfig.KAKAO_ADMIN_KEY}",
        @Body message: PushNotification
    )
}