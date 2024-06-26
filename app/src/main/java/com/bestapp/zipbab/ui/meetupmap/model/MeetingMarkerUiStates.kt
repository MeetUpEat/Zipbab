package com.bestapp.zipbab.ui.meetupmap.model

import com.naver.maps.map.overlay.Marker

data class MeetingMarkerUiStates(
    val meetingMarkers: List<Marker> = emptyList(),
    val isMapReady: Boolean = false
)