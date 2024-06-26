package com.bestapp.zipbab.data.notification.setup

import com.bestapp.zipbab.data.notification.refresh.Refresh
import com.bestapp.zipbab.data.notification.refresh.RefreshReturn
import retrofit2.http.Body
import retrofit2.http.POST

interface GooGleRefreshService {
    @POST("o/oauth2/v2/auth")
    fun getRefreshTokenInformation(
        @Body refresh: Refresh
    ) : RefreshReturn
}