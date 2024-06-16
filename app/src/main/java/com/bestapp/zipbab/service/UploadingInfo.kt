package com.bestapp.zipbab.service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface UploadingInfo : Parcelable {

    @Parcelize
    data class Post(
        val userDocumentID: String,
        val images: List<String>
    ): Parcelable
}