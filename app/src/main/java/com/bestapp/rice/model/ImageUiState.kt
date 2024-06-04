package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Image
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUiState(
    val url: String,
): Parcelable {

    companion object {
        fun createFrom(image: Image) = ImageUiState(
            url = image.url
        )
    }
}
