package com.bestapp.zipbab.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bestapp.zipbab.R
import com.bestapp.zipbab.util.redirectUserToSetting
import com.bestapp.zipbab.util.showAlertDialog

class PermissionManager(
    private val fragment: Fragment,
) {
    private val readImagePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    fun isFullImageAccessGranted(): Boolean {
        return isFullAccessGranted() || isFullAccessGrantedUpToAPI32()
    }

    fun requestFullImageAccessPermission(
        requestMultiplePermissionLauncher: ActivityResultLauncher<Array<String>>,
        onGranted: () -> Unit,
    ) {
        if (isFullAccessGranted()) {
            onGranted()
        } else if (isPartialAccessGranted()) {
            // 이미 부분적 권한을 허용한 경우, 전체 권한을 받으려면 사용자가 설정창에 직접 가서 변경하는 방법 밖에 없음
            fragment.requireContext().redirectUserToSetting()
        } else if (isFullAccessGrantedUpToAPI32()) {
            onGranted()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    readImagePermission.first()
                )
            ) {
                showPermissionExplanationDialog(true) {
                    requestMultiplePermissionLauncher.launch(readImagePermission)
                }
            } else {
                requestMultiplePermissionLauncher.launch(readImagePermission)
            }
        }
    }

    fun requestPartialImageAccessPermission(
        requestMultiplePermissionLauncher: ActivityResultLauncher<Array<String>>,
        isImageReselect: Boolean,
        onGranted: () -> Unit,
    ) {
        if (isPartialAccessGranted()) {
            if (isImageReselect) {
                requestMultiplePermissionLauncher.launch(readImagePermission)
            } else {
                onGranted()
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    readImagePermission.last()
                )
            ) {
                showPermissionExplanationDialog(true) {
                    requestMultiplePermissionLauncher.launch(readImagePermission)
                }
            } else {
                requestMultiplePermissionLauncher.launch(readImagePermission)
            }
        }
    }

    fun showPermissionExplanationDialog(
        isPossibleToShowPermission: Boolean = false,
        callback: () -> Unit = {}
    ) {
        fragment.requireContext().showAlertDialog(
            title = ContextCompat.getString(
                fragment.requireContext(),
                R.string.image_permission_title
            ),
            message = ContextCompat.getString(
                fragment.requireContext(),
                R.string.image_permission_message
            ),
            negativeMessage = ContextCompat.getString(
                fragment.requireContext(),
                R.string.image_permission_negative
            ),
            positiveMessage = ContextCompat.getString(
                fragment.requireContext(),
                R.string.image_permission_positive
            ),
            onNegativeSelect = {},
            onPositiveSelect = {
                if (isPossibleToShowPermission) {
                    callback()
                    return@showAlertDialog
                }
                fragment.requireContext().redirectUserToSetting()
            }
        )
    }

    private fun isFullAccessGrantedUpToAPI32(): Boolean = ContextCompat.checkSelfPermission(
        fragment.requireContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun isFullAccessGranted(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

    private fun isPartialAccessGranted(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == PackageManager.PERMISSION_GRANTED
}