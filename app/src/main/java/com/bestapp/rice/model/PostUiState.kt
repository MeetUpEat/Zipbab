package com.bestapp.rice.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostUiState(
    val postDocumentID: String,
    val images: List<String>,
) : Parcelable
