package com.bestapp.zipbab.model

data class PostUiState(
    val postDocumentID: String = "",
    val images: List<String> = emptyList(),
    val state: UploadState = UploadState.Default(postDocumentID),
)
