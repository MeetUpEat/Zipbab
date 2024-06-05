package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Image
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUiState(
    val url: String,
) : Parcelable {

    fun toData() = Image(
        url = this.url
    )

    fun isEmpty(): Boolean = this == EMPTY

    companion object {

        val EMPTY = ImageUiState("")

        fun createFrom(image: Image) = ImageUiState(
            url = image.url
        )
    }
}
