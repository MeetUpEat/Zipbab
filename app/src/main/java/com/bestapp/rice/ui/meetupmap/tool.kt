package com.bestapp.rice.ui.meetupmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

suspend fun toBitmap(context: Context, uri: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        (result as? BitmapDrawable)?.bitmap?.let { toRoundedBitmap(it) }
    }
}

private fun toRoundedBitmap(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    // 하드웨어 비트맵을 소프트웨어 비트맵으로 변환
    val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint()
    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val path = Path()


    path.addOval(rect, Path.Direction.CCW) // Path 객체를 이용해 원형 경로를 생성
    // paint.isAntiAlias = true // 안티 앨리어싱 활성화 - 가장자리를 부드럽게 만들어 준다.
    canvas.clipPath(path) // 캔버스에 원형 경로를 클리핑 마스크로 설정
    canvas.drawBitmap(softwareBitmap, 0f, 0f, paint) // 원본 비트맵을 캔버스에 그리기

    return output
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // 지구 반지름 (킬로미터)
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c // 거리 (킬로미터)
}

