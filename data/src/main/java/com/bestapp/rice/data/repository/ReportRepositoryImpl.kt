package com.bestapp.rice.data.repository

import com.bestapp.rice.data.FirestorDB.FirestoreDB
import com.bestapp.rice.data.model.remote.ReportForm
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firestoreDB: FirestoreDB,
) : ReportRepository {
    override suspend fun reportUser(userDocumentID: String) {
        firestoreDB.getReportDB()
            .add(ReportForm(userDocumentID= userDocumentID))
            .await()
    }

    override suspend fun reportPost(userDocumentID: String, postDocumentID: String) {
        firestoreDB.getReportDB()
            .add(ReportForm(userDocumentID = userDocumentID, postDocumentID = postDocumentID))
            .await()
    }
}