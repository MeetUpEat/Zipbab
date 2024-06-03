package com.bestapp.rice.data.notification

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
    fun registerToken(
        @Header("Authorization") key: String = "어드민 키",
        @Field("uuid") uuid: String,
        @Field("device_id") deviceId: String,
        @Field("push_type") pushType: String,
        @Field("push_token") pushToken: String
    ) : RegisterToken

    @GET("v2/push/tokens")
    fun downloadToken(
        @Header("Authorization") key: String = "어드민 키",
        @Query("uuid") uuid: String
    ) : DownloadToken

    @FormUrlEncoded
    @POST("v2/push/deregister")
    fun deleteToken(
        @Header("Authorization") key: String = "어드민 키",
        @Field("uuid") uuid: String,
        @Field("device_id") deviceId: String,
        @Field("push_type") pushType: String
    )

    @FormUrlEncoded
    @POST("v2/push/send")
    fun sendNotification(
        @Header("Authorization") key: String = "어드민 키",
        @Field("uuids") uuids : List<String>,
        @Body pushMessage : PushMsgJson,
        @Field("bypass") bypass: Boolean
    ) : SendMsg
}