package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Image
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUiState(
    val imageDocumentId: String,
    val url: String,
) : Parcelable {

    fun toData(): Image = Image(
        imageDocumentId = imageDocumentId,
        url = url,
    )

    companion object {
        fun createFrom(image: Image) = ImageUiState(
            imageDocumentId = image.imageDocumentId,
            url = image.url,
        )
    }
}
