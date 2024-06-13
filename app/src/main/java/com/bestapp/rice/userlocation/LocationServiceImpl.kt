package com.bestapp.rice.userlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.type.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class LocationServiceImpl @Inject constructor(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient,
) : LocationService {

    // 함수 내에서 처음에 권한 체크를 하지만, 아래에서 추가적으로 권한체크를 요구하여 추가함
    @SuppressLint("MissingPermission")
    override suspend fun requestLocation(): LatLng? {
        if (!context.hasLocationPermission()) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            val request = LocationRequest.Builder(CYCLE_TIME)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let {
                        val latlng = createLatLng(it.latitude, it.longitude)
                        Log.d("사용자 위치 : LatLng", latlng.toString())

                        if (continuation.isActive) {
                            continuation.resume(latlng) {
                                locationClient.removeLocationUpdates(this)
                            }
                        }
                    }
                }
            }

            locationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            ).addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resumeWithException(it)
                }
            }

            continuation.invokeOnCancellation {
                locationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun createLatLng(latitude: Double, longitude: Double): LatLng {
        return LatLng.newBuilder()
            .setLatitude(latitude)
            .setLongitude(longitude)
            .build()
    }

    companion object {
        val CYCLE_TIME = 10_000L
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