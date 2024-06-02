package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.bestapp.rice.data.network.FirebaseClient
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.google.type.DateTime
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class StorageRepositoryImpl: StorageRepository {
    private val currentTime: Long
        get() = System.currentTimeMillis()

    override suspend fun uploadImage(imageUri: Uri): String {
        val imageRef = storageRef.child("${currentTime}.jpg")

        imageRef.putFile(imageUri).await()

        return downloadImage(imageRef)
    }

    override suspend fun uploadImage(imageBitmap: Bitmap): String {
        val imageRef = storageRef.child("${currentTime}.jpg")

        // Bitmap to ByteArray
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // FireStorage, 이미지 업로드
        imageRef.putBytes(data).await()

        return downloadImage(imageRef)
    }

    override suspend fun downloadImage(storageRef: StorageReference): String {
        // FireStorage, 이미지 다운로드 Url 받기
        val downloadImageUri = storageRef.downloadUrl.await()

        return downloadImageUri.toString()
    }

    companion object {
        val storageRef = FirebaseClient.imageStorageService
    }
}