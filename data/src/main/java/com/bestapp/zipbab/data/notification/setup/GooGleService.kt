package com.bestapp.zipbab.data.notification.setup

import com.bestapp.zipbab.data.notification.access.Access
import com.bestapp.zipbab.data.notification.access.AccessReturn
import retrofit2.http.Body
import retrofit2.http.POST

interface GooGleService {
    @POST("token")
    suspend fun getTokenInformation(
        @Body access: Access
    ) : AccessReturn
}