package com.bestapp.zipbab.data.model

sealed interface UploadStateEntity {

    val tempPostDocumentID: String

    data class Pending(
        override val tempPostDocumentID: String
    ): UploadStateEntity

    data class ProcessImage(
        override val tempPostDocumentID: String,
        val currentProgressOrder: Int, // 현재 업로드 중인 이미지 순서, 1부터 시작
        val maxOrder: Int, // 전체 이미지 순서
    ): UploadStateEntity

    data class ProcessPost(
        override val tempPostDocumentID: String,
    ): UploadStateEntity
    data class Fail(
        override val tempPostDocumentID: String,
    ): UploadStateEntity
    data class SuccessPost(
        override val tempPostDocumentID: String,
        val postDocumentID: String,
    ): UploadStateEntity
}