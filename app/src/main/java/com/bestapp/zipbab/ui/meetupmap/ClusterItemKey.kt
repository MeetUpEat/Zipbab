package com.bestapp.zipbab.ui.meetupmap

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

data class ClusterItemKey(
    val meetingDocumentID: Int,
    private val position: LatLng
) : ClusteringKey {
    override fun getPosition() = position
}