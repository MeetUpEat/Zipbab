package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.notification.access.AccessReturn
import com.bestapp.zipbab.data.notification.refresh.RefreshReturn

interface ProviderRepository {
    suspend fun getTokenInfo(id: String, secret: String, code: String, token: String, type: String) : AccessReturn

    suspend fun getRefreshInfo(id: String, secret: String, code: String, type: String, uri: String) : RefreshReturn
}