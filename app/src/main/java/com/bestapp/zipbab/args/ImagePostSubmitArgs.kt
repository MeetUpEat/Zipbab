package com.bestapp.zipbab.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImagePostSubmitArgs(
    val userDocumentID: String,
    val images: List<String>,
): Parcelable
