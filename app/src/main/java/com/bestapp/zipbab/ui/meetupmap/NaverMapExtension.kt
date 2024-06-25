package com.bestapp.zipbab.ui.meetupmap

import android.content.Context
import android.location.Location
import com.bestapp.zipbab.R
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

fun NaverMap.moveToPosition(lastLocation: Location) = moveToPosition(LatLng(lastLocation.latitude, lastLocation.longitude))

fun NaverMap.moveToPosition(latLng: LatLng) {
    val cameraPosition = CameraPosition(latLng, 15.0)
    val cameraUpdate = CameraUpdate.toCameraPosition(cameraPosition)

    moveCamera(cameraUpdate)
}

fun NaverMap.addMeetingMarkers(
    context: Context,
    meetUpMapUiState: MeetUpMapUiState,
    goMeetingDetailFragment: (String, Boolean) -> Unit
): List<Marker> {
    var markerList = MutableList<Marker>(meetUpMapUiState.meetUpMapMeetingUis.size) { Marker() }
    var lastOpenedInfoWindow : InfoWindow? = null

    /** return 값
     *  true : 이벤트 소비, OnMapClick 이벤트는 발생하지 않음 (다르게 생각하면, 이벤트 처리 완료)
     *  false : 이벤트 전파, OnMapClick 이벤트가 발생함 (다르게 생각하면, 추가 이벤트 처리 필요)
     */
    val infoWindowClickListener = Overlay.OnClickListener { overlay ->
        val marker = overlay as Marker

        val index = marker.tag as Int
        val meetUpMapMeeting = meetUpMapUiState.meetUpMapMeetingUis[index]
        val infoWindow = createInfoWindow(context, meetUpMapMeeting.toContent())

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

        infoWindow.setOnClickListener {
            goMeetingDetailFragment(
                meetUpMapMeeting.meetingDocumentID,
                meetUpMapMeeting.isHost
            )
            true
        }
        true
    }

    meetUpMapUiState.meetUpMapMeetingUis.forEachIndexed { index, meetUpMapMeeting ->
        val placeLocationUi = meetUpMapMeeting.placeLocationArgs
        val latLng = LatLng(placeLocationUi.locationLat.toDouble(), placeLocationUi.locationLong.toDouble())

        val marker = markerList[index]
        val markerWidth = 170
        val sizeScale = 1.95f

        marker.icon = OverlayImage.fromResource(R.drawable.ic_maker_meeting)
        marker.width = markerWidth // Marker.SIZE_AUTO
        marker.height = markerWidth * sizeScale.toInt() // Marker.SIZE_AUTO
        marker.position = latLng
        marker.tag = index
        marker.onClickListener = infoWindowClickListener

        marker.captionText = meetUpMapMeeting.shortTitle
        marker.captionRequestedWidth = 200 // textview width
        marker.captionTextSize = 13f
    }

    return markerList.toList()
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

private fun MeetUpMapUi.toContent(): String {
    val mainMenu = String.format("메인메뉴 : %s", mainMenu)
    val costByPerson = String.format("1인당 비용 : %,d원", costValueByPerson)
    val goMeeting = "모임 보러 가기 ->"

    val contents = MutableList<String>(0) { "" }

    contents.add(mainMenu)
    contents.add(costByPerson)
    contents.add(goMeeting)

    return contents.joinToString("\n")
}