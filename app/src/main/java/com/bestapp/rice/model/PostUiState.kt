package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Post
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostUiState(
    val postDocumentID: String,
    val images: List<String>,
) : Parcelable {
    companion object {
        fun createFrom(post: Post) = PostUiState(
            postDocumentID = post.postDocumentID,
            images = post.images,
        )
    }
}
