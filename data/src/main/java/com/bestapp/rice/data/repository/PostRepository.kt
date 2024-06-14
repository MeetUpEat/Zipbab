package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Post

interface PostRepository {
    suspend fun getPosts(userDocumentID: String): List<Post>
    suspend fun getPost(postDocumentID: String): Post
    suspend fun deletePost(userDocumentID: String, postDocumentID: String): Boolean
}