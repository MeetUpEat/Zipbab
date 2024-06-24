package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.FirestoreDB.FirestoreDB
import com.bestapp.zipbab.data.doneSuccessful
import com.bestapp.zipbab.data.model.remote.PostResponse
import com.bestapp.zipbab.data.model.remote.UserResponse
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class PostRepositoryImpl @Inject constructor(
    private val firestoreDB: FirestoreDB,
    private val storageRepository: StorageRepository,
) : PostRepository {
    override suspend fun getPosts(userDocumentID: String): List<PostResponse> {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("userDocumentID", userDocumentID)
            .get()
            .await()

        val userResponse = users.first().toObject<UserResponse>()

        val postResponses = mutableListOf<PostResponse>()
        for (postDocumentId in userResponse.posts) {
            val responses = firestoreDB.getPostDB()
                .whereEqualTo("postDocumentID", postDocumentId)
                .get()
                .await()

            postResponses.add(responses.first().toObject<PostResponse>())
        }
        return postResponses
    }

    override suspend fun getPost(postDocumentID: String): PostResponse {
        val posts = firestoreDB.getPostDB()
            .whereEqualTo("postDocumentID", postDocumentID)
            .get()
            .await()

        return posts.first().toObject<PostResponse>()
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