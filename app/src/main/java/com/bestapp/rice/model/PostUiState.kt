package com.bestapp.rice.model

data class PostUiState(
    val postDocumentID: String = "",
    val images: List<String> = emptyList(),
    val state: UploadState = UploadState.Default,
)
