package com.bestapp.zipbab.permission

import android.Manifest

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment


class LocationPermissionManager(
    private val fragment: Fragment,
    private val locationPermissionSnackBar: LocationPermissionSnackBar,
) {
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val locationPermissionRequest = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val coarseLocationPermission =
            permissions?.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ?: false
        val fineLocationPermission =
            permissions?.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ?: false

        if (!coarseLocationPermission || !fineLocationPermission) {
            locationPermissionSnackBar.showPermissionSettingSnackBar()
        }
    }

    fun requestPermission() = locationPermissionRequest.launch(locationPermissions)
}