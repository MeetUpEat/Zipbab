package com.bestapp.zipbab.userlocation

import com.google.type.LatLng

interface LocationService {
    suspend fun requestLocation(): LatLng?
}