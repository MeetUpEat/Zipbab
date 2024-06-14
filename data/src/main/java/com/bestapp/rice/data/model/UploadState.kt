package com.bestapp.rice.data.model

sealed interface UploadState {
    data object Pending: UploadState
    data class ProcessImage(
        val currentProgressOrder: Int, // 현재 업로드 중인 이미지 순서, 1부터 시작
        val maxOrder: Int, // 전체 이미지 순서
    ): UploadState

    data object ProcessPost: UploadState
    data object Fail: UploadState
    data class SuccessPost(val postDocumentID: String): UploadState
}