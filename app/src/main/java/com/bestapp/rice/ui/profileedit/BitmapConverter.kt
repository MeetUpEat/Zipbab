package com.bestapp.rice.ui.profileedit

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * // Hilt 쓸때는 Singleton이 아닌 viewModelScoped로 지정
 */
class BitmapConverter(
    private val contentResolver: ContentResolver,
) {

    /**
     * Dispatchers.Default로 동작합니다.
     */
    suspend fun convert(uri: String): Bitmap {
        return withContext(Dispatchers.Default) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        contentResolver,
                        uri.toUri()
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri.toUri())
            }
        }
    }
}