package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.StorageReference

interface StorageRepository {
    suspend fun uploadImage(imageUri: Uri): String

    suspend fun uploadImage(imageBitmap: Bitmap): String

    suspend fun downloadImage(storageRef: StorageReference): String
}

