package com.bestapp.rice.userlocation

import com.google.type.LatLng

interface LocationService {
    suspend fun requestLocation(): LatLng?
}