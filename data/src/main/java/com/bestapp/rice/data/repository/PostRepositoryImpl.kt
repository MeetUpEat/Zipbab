package com.bestapp.rice.data.repository

import com.bestapp.rice.data.FirestorDB.FirestoreDB
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.User
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class PostRepositoryImpl @Inject constructor(
    private val firestoreDB: FirestoreDB,
) : PostRepository {
    override suspend fun getPosts(userDocumentID: String): List<Post> {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("userDocumentID", userDocumentID)
            .get()
            .await()

        val user = users.first().toObject<User>()

        val posts = mutableListOf<Post>()
        for (postDocumentId in user.posts) {
            val responses = firestoreDB.getPostDB()
                .whereEqualTo("postDocumentID", postDocumentId)
                .get()
                .await()

            posts.add(responses.first().toObject<Post>())
        }
        return posts
    }

    override suspend fun getPost(postDocumentID: String): Post {
        val posts = firestoreDB.getPostDB()
            .whereEqualTo("postDocumentID", postDocumentID)
            .get()
            .await()

        return posts.first().toObject<Post>()
    }
}