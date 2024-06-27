package com.bestapp.zipbab.ui.meetupmap

import android.content.Context
import android.location.Location
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.bestapp.zipbab.R
import com.bestapp.zipbab.ui.meetupmap.model.MeetUpMapUi
import com.bestapp.zipbab.ui.meetupmap.model.MeetUpMapUiState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun haversine(latlng1: LatLng, latlng2: LatLng) =
    haversine(latlng1.latitude, latlng1.longitude, latlng2.latitude, latlng2.longitude)

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

fun NaverMap.switchNightMode(isEnable: Boolean) {
    setNightModeEnabled(isEnable)
}

fun NaverMap.switchNightMode(isEnable: Boolean, meetingMarkers: List<Marker>) {
    setNightModeEnabled(isEnable)

    meetingMarkers.forEach { marker ->
        marker.switchNightMode(isEnable)
    }
}

fun Marker.switchNightMode(isEnable: Boolean) {
    if (isEnable) {
        captionColor = Color.White.toArgb()
        captionHaloColor = Color.Black.toArgb()
    } else {
        captionColor = Color.Black.toArgb()
        captionHaloColor = Color.White.toArgb()
    }
}

fun NaverMap.moveToPosition(lastLocation: Location) = moveToPosition(LatLng(lastLocation.latitude, lastLocation.longitude))

fun NaverMap.moveToPosition(latLng: LatLng) {
    val defaultZoomLevel = 15.0

    val cameraPosition = CameraPosition(latLng, defaultZoomLevel)
    val cameraUpdate = CameraUpdate.toCameraPosition(cameraPosition)

    moveCamera(cameraUpdate)
}

fun Marker.setProperty(
    context: Context,
    meetUpMapUi: MeetUpMapUi,
    goMeetingDetailFragment: (String, Boolean) -> Unit
) {
    val markerWidth = 170
    val markerSizeScale = 1.95f
    val textViewWidth = 200
    val textSize = 13f

    val placeLocationUi = meetUpMapUi.placeLocationArgs
    val latLng = LatLng(placeLocationUi.locationLat.toDouble(), placeLocationUi.locationLong.toDouble())

    icon = OverlayImage.fromResource(R.drawable.ic_maker_meeting)
    width = markerWidth
    height = markerWidth * markerSizeScale.toInt()
    position = latLng

    captionText = meetUpMapUi.shortTitle
    captionRequestedWidth = textViewWidth
    captionTextSize = textSize

    val makerClickListenerInfoWindow = setInfoWindow(context, meetUpMapUi, goMeetingDetailFragment)
    onClickListener = makerClickListenerInfoWindow
}

private fun setInfoWindow(
    context: Context,
    meetUpMapUi: MeetUpMapUi,
    goMeetingDetailFragment: (String, Boolean) -> Unit,
): Overlay.OnClickListener {
    var lastOpenedInfoWindow : InfoWindow? = null

    return Overlay.OnClickListener { overlay ->
        val marker = overlay as Marker

        val infoWindow = createInfoWindow(context, meetUpMapUi.toContent(context))

        if (infoWindow.isAdded) {
            infoWindow.close()
        } else {
            infoWindow.open(marker)

            if (lastOpenedInfoWindow == null) {
                lastOpenedInfoWindow = infoWindow
            } else {
                lastOpenedInfoWindow!!.close()
                lastOpenedInfoWindow = infoWindow
            }
        }

        /** return Boolean 값
         *  true : 이벤트 소비, OnMapClick 이벤트는 발생하지 않음 (다르게 생각하면, 이벤트 처리 완료)
         *  false : 이벤트 전파, OnMapClick 이벤트가 발생함 (다르게 생각하면, 추가 이벤트 처리 필요)
         */
        infoWindow.setOnClickListener {
            goMeetingDetailFragment(
                meetUpMapUi.meetingDocumentID,
                meetUpMapUi.isHost
            )
            true
        }
        true
    }
}

private fun createInfoWindow(context: Context, content: String): InfoWindow {
    return InfoWindow().apply {
        adapter = object : InfoWindow.DefaultTextAdapter(context) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return content
            }
        }
    }
}

private fun MeetUpMapUi.toContent(
    context: Context
): String {
    val mainMenu = context.getString(R.string.meet_up_map_main_menu).format(mainMenu)
    val costByPerson = context.getString(R.string.meet_up_map_cost_by_person).format(costValueByPerson)
    val goMeeting = context.getString(R.string.meet_up_map_go_meeting_info)

    val contents = ArrayList<String>(0)

    contents.add(mainMenu)
    contents.add(costByPerson)
    contents.add(goMeeting)

    return contents.joinToString("\n")
}