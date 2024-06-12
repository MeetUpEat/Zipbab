package com.bestapp.rice.ui.meetupmap

import android.graphics.Color
import com.bestapp.rice.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import kotlin.math.*


/** 사용자 위치를 파란색 원 아이콘으로 표시하도록 해주는 함수
 *
 */
fun KakaoMap.createUserLabel(latLng: LatLng): Label {
    var styles = createLabelStyles(R.drawable.ic_maker_user, "meeting")

    // 라벨 스타일 추가
    this.labelManager!!.addLabelStyles(styles)

    val pos = LatLng.from(
        latLng.getLatitude(),
        latLng.getLongitude()
    )

    val labelOptions = LabelOptions.from(pos)
        .setStyles(styles)
        .setTexts("내 위치")

    return this.labelManager!!.layer!!.addLabel(labelOptions)
}

/** LabelStyles : 지도의 확대/축소 줌레벨 마다 각각 다른 LabelStyle을 적용할 수 있음
 * Min ZoomLevel ~ 7 까지,
 * 8 ~ 10 까지              : bitmap 이미지 나옴
 * 11 ~ 14 까지             : bitmap 이미지 나옴
 * 15 ~ Max ZoomLevel 까지  : bitmap 이미지와 텍스트 나옴
 */

fun KakaoMap.createMeetingLabels(meetUpMapUiState: MeetUpMapUiState): List<Label> {
    var labels = ArrayList<Label>(meetUpMapUiState.meetUpMapMeetingUis.size)

    meetUpMapUiState.meetUpMapMeetingUis.forEach {
        // notice: 라벨 하나당 고유의 스타일을 가져야함
        var styles = createLabelStyles(R.drawable.ic_maker_meeting, "meeting-${it.meetingDocumentID}")

        // 라벨 스타일 추가
        this.labelManager!!.addLabelStyles(styles)

        val pos = LatLng.from(
            it.placeLocationUi.locationLat.toDouble(),
            it.placeLocationUi.locationLong.toDouble()
        )

        val labelOptions = LabelOptions.from(pos)
            .setStyles(styles)
            .setTexts(it.title)

        val label = this.labelManager!!.layer!!.addLabel(labelOptions)

        labels.add(label)
    }

    return labels.toList()
}

fun KakaoMap.moveToCamera(lat: Double, long: Double) = moveToCamera(LatLng.from(lat, long))

fun KakaoMap.moveToCamera(latLng: LatLng) {
    if (latLng.latitude == MeetUpMapFragment.ZERO && latLng.longitude == MeetUpMapFragment.ZERO) {
        return
    }

    val cameraUpdatePosition = CameraUpdateFactory.newCenterPosition(latLng, 15)
    val cameraAnimation = CameraAnimation.from(10, true, true)

    this.moveCamera(cameraUpdatePosition, cameraAnimation)
}

fun createLabelStyles(drawable: Int, styleID: String): LabelStyles {
    return LabelStyles.from(
        styleID,
        LabelStyle.from(drawable).setZoomLevel(11)
            .setTextStyles(24, Color.BLACK, 1, Color.GRAY),
        LabelStyle.from(drawable).setZoomLevel(15)
            .setTextStyles(30, Color.BLACK, 1, Color.GRAY)
    )
}

fun haversine(latlng1: LatLng, latlng2: LatLng) = haversine(latlng1.latitude, latlng1.longitude, latlng2.latitude, latlng2.longitude)

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

//suspend fun toBitmap(context: Context, uri: String): Bitmap? {
//    return withContext(Dispatchers.IO) {
//        val loader = ImageLoader(context)
//        val request = ImageRequest.Builder(context)
//            .data(uri)
//            .build()
//
//        val result = (loader.execute(request) as? SuccessResult)?.drawable
//        (result as? BitmapDrawable)?.bitmap?.let { toRoundedBitmap(it) }
//    }
//}
//
//private fun toRoundedBitmap(bitmap: Bitmap): Bitmap {
//    val width = 76 // bitmap.width
//    val height = 76 // bitmap.height
//
//    // 하드웨어 비트맵을 소프트웨어 비트맵으로 변환
//    val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//
//    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(output)
//
//    val paint = Paint()
//    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
//    val path = Path()
//
//    path.addOval(rect, Path.Direction.CCW) // Path 객체를 이용해 원형 경로를 생성
//    // paint.isAntiAlias = true // 안티 앨리어싱 활성화 - 가장자리를 부드럽게 만들어 준다.
//    canvas.clipPath(path) // 캔버스에 원형 경로를 클리핑 마스크로 설정
//    canvas.drawBitmap(softwareBitmap, 0f, 0f, paint) // 원본 비트맵을 캔버스에 그리기
//
//    return output
//}