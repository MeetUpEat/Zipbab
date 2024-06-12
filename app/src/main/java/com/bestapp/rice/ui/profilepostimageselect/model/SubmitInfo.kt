package com.bestapp.rice.ui.profilepostimageselect.model

import com.bestapp.rice.service.UploadingInfo

data class SubmitInfo(
    val userDocumentID: String,
    val images: List<String>,
) {
    fun toInfo() = UploadingInfo.Post(
        userDocumentID = userDocumentID,
        images = images,
    )
}