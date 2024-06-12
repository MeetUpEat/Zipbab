package com.bestapp.rice.Location

import com.google.type.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationService {
    fun requestLocationUpdates(): Flow<LatLng?>
}