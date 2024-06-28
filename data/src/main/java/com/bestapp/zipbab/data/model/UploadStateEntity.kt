package com.bestapp.zipbab.data.model

import com.squareup.moshi.JsonClass

sealed interface UploadStateEntity {

    val tempPostDocumentID: String

    @JsonClass(generateAdapter = true)
    data class Pending(
        override val tempPostDocumentID: String
    ): UploadStateEntity

    @JsonClass(generateAdapter = true)
    data class ProcessImage(
        override val tempPostDocumentID: String,
        val currentProgressOrder: Int, // 현재 업로드 중인 이미지 순서, 1부터 시작
        val maxOrder: Int, // 전체 이미지 순서
    ): UploadStateEntity

    @JsonClass(generateAdapter = true)
    data class ProcessPost(
        override val tempPostDocumentID: String,
    ): UploadStateEntity

    @JsonClass(generateAdapter = true)
    data class Fail(
        override val tempPostDocumentID: String,
    ): UploadStateEntity

    @JsonClass(generateAdapter = true)
    data class SuccessPost(
        override val tempPostDocumentID: String,
        val postDocumentID: String,
    ): UploadStateEntity
}