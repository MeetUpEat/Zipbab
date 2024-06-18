package com.bestapp.zipbab.userlocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.type.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationServiceImpl @Inject constructor(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient,
) : LocationService {

    override suspend fun requestLocation(): LatLng? {
        var LatLng: LatLng? = null

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LatLng = suspendCancellableCoroutine { continuation ->
                locationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).addOnSuccessListener { location ->
                    location?.let {
                        val latlng = createLatLng(it.latitude, it.longitude)

                        continuation.resume(latlng)
                    } ?: run {
                        continuation.resume(null)
                    }
                }.addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resumeWithException(it)
                    }
                }
            }
        }

        return LatLng
    }

    private fun createLatLng(latitude: Double, longitude: Double): LatLng {
        return LatLng.newBuilder()
            .setLatitude(latitude)
            .setLongitude(longitude)
            .build()
    }
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}