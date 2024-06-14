package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.FirestorDB.FirestoreDB
import com.bestapp.zipbab.data.doneSuccessful
import com.bestapp.zipbab.data.model.remote.Post
import com.bestapp.zipbab.data.model.remote.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class PostRepositoryImpl @Inject constructor(
    private val firestoreDB: FirestoreDB,
    private val storageRepository: StorageRepository,
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

    override suspend fun deletePost(userDocumentID: String, postDocumentID: String): Boolean {
        val post = getPost(postDocumentID)

        // 포스트에 사용된 이미지 삭제
        for (image in post.images) {
            storageRepository.deleteImage(image)
        }

        // 포스트 삭제
        firestoreDB.getPostDB().document(postDocumentID).delete()

        // 사용자 정보에서 해당 포스트 삭제
        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("posts", FieldValue.arrayRemove(postDocumentID))
            .doneSuccessful()
    }
}