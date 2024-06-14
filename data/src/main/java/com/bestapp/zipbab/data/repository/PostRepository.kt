package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.model.remote.Post

interface PostRepository {
    suspend fun getPosts(userDocumentID: String): List<Post>
    suspend fun getPost(postDocumentID: String): Post
    suspend fun deletePost(userDocumentID: String, postDocumentID: String): Boolean
}