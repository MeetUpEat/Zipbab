package com.bestapp.rice.data.repository

interface ReportRepository {

    suspend fun reportUser(userDocumentID: String)
    suspend fun reportPost(userDocumentID: String, postDocumentID: String)
}