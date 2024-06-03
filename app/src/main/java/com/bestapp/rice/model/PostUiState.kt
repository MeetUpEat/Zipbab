package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Post
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostUiState(
    val postDocumentID: String,
    val imageUiStates: List<ImageUiState>,
) : Parcelable {

    fun toData() = Post(
        postDocumentID = postDocumentID,
        images = images,
    )

    companion object {
        fun createFrom(post: Post) = PostUiState(
            postDocumentID = post.postDocumentID,
            imageUiStates = post.images.map {
                ImageUiState.createFrom(it)
            },
        )
    }
}
