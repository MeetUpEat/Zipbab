package com.bestapp.zipbab.model

sealed interface UploadState {

    val tempPostDocumentID: String

    data class Default(
        override val tempPostDocumentID: String,
    ): UploadState
    data class Pending(
        override val tempPostDocumentID: String,
    ): UploadState
    data class InProgress(
        override val tempPostDocumentID: String,
        val currentProgressOrder: Int, // 현재 업로드 중인 이미지 순서, 1부터 시작
        val maxOrder: Int, // 전체 이미지 순서
    ): UploadState
    data class ProcessPost (
        override val tempPostDocumentID: String,
    ): UploadState
    data class Fail(
        override val tempPostDocumentID: String,
    ): UploadState
    data class SuccessPost(
        override val tempPostDocumentID: String,
        val postDocumentID: String
    ): UploadState
}