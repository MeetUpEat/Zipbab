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

fun NaverMap.addMeetingMarkers(context: Context, meetUpMapUiState: MeetUpMapUiState): List<Marker> {
    var markerList = MutableList<Marker>(meetUpMapUiState.meetUpMapMeetingUis.size) { Marker() }

    meetUpMapUiState.meetUpMapMeetingUis.forEachIndexed { index, meetUpMapMeeting ->
        val placeLocationUi = meetUpMapMeeting.placeLocationUi
        val latLng = LatLng(placeLocationUi.locationLat.toDouble(), placeLocationUi.locationLong.toDouble())

        val marker = markerList[index]
        val markerWidth = 170
        val sizeScale = 1.95f


        marker.icon = OverlayImage.fromResource(R.drawable.ic_maker_meeting)
        marker.width = markerWidth // Marker.SIZE_AUTO
        marker.height = markerWidth * sizeScale.toInt() // Marker.SIZE_AUTO
        marker.position = latLng
        marker.tag = meetUpMapMeeting.meetingDocumentID
        marker.map = this

        marker.setOnClickListener {
            // TODO("it.tag에 meetingDocumentID값 백업")
            val meetingDocumentID = it.tag as String

            false
        }

        val contentString = """
            ${meetUpMapMeeting.title} | 123
            """.trimIndent()

        marker.setOnClickListener {
            val infoWindow = InfoWindow().apply {
                adapter = object : InfoWindow.DefaultTextAdapter(context) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return contentString
                    }
                }
            }

            if (infoWindow.isAdded) {
                infoWindow.close()
            } else {
                infoWindow.open(marker)
            }
            true
        }
    }

    return markerList.toList()
}