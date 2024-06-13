package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.bestapp.rice.data.FirestorDB.FirestoreDB
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


internal class StorageRepositoryImpl @Inject constructor(
    private val firestoreDB: FirestoreDB
): StorageRepository {
    private val currentTime: Long
        get() = System.currentTimeMillis()

    private val imageNamePattern: Pattern = Pattern.compile("images%2F([0-9]+\\.jpg)")

    override suspend fun uploadImage(imageUri: Uri): String {
        val imageRef = firestoreDB.getImagesDB().child("${currentTime}.jpg")

        imageRef.putFile(imageUri).await()

        return downloadImage(imageRef)
    }

    override suspend fun uploadImage(imageBitmap: Bitmap): String {
        val imageRef = firestoreDB.getImagesDB().child("${currentTime}.jpg")

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

    override suspend fun deleteImage(imageUrl: String) {
        val fileName = extractFilename(imageUrl) ?: return
        val fileRef = firestoreDB.getImagesDB().child(fileName)

        fileRef.delete()
    }

    private fun extractFilename(url: String): String? {
        val matcher: Matcher = imageNamePattern.matcher(url)

        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }

}