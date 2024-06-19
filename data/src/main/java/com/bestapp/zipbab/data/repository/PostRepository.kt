package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.model.remote.PostResponse

interface PostRepository {
    suspend fun getPosts(userDocumentID: String): List<PostResponse>
    suspend fun getPost(postDocumentID: String): PostResponse
    suspend fun deletePost(userDocumentID: String, postDocumentID: String): Boolean
}