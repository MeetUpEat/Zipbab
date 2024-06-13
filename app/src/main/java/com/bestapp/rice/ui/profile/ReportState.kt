package com.bestapp.rice.ui.profile

sealed interface ReportState {
    data object Default : ReportState

    data class ProgressProfile(
        val userDocumentID: String,
    ): ReportState

    data class PendingPost(
        val userDocumentID: String,
        val postDocumentID: String,
        val isSelfProfile: Boolean,
    ): ReportState

    data class ProgressPost(
        val postDocumentID: String,
    ): ReportState

    data object Complete : ReportState

    data object Fail: ReportState
}