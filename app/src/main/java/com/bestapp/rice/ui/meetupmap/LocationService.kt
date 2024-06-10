package com.bestapp.rice.ui.meetupmap

import com.google.type.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationService {
    fun requestLocationUpdates(): Flow<LatLng?>
}