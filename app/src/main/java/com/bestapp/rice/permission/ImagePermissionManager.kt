package com.bestapp.rice.permission

import android.Manifest
import android.content.ContentUris
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bestapp.rice.R
import com.bestapp.rice.ui.profileimageselect.GalleryImageInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * registerForActivity 때문에 onCreate 이전에 초기화 되어야 합니다.
 * @param scope : CoroutineScope을 전달하는 것은 위험하다. 추후 반드시 수정해야 한다.
 */
class ImagePermissionManager(
    private val fragment: Fragment,
) {
    private lateinit var scope: CoroutineScope

    private var onGranted: (List<GalleryImageInfo>) -> Unit = {}
    private var isImageReselect = false

    private val requestMultiplePermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantsInfo ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (grantsInfo[Manifest.permission.READ_MEDIA_IMAGES] == true) {
                    scope.launch {
                        val images = getImageFromGallery()
                        onGranted(images)
                    }
                } else if (grantsInfo[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true) {
                    scope.launch {
                        val images = getImageFromGallery()
                        onGranted(images)
                    }
                } else {
                    showPermissionExplanation()
                }
            } else if (grantsInfo.values.first()) {
                scope.launch {
                    val images = getImageFromGallery()
                    onGranted(images)
                }
            } else {
                showPermissionExplanation()
            }
        }

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

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    @Deprecated("callback이 아닌 Coroutine으로 바꿔야 함")
    fun requestFullImageAccessPermission(
        onGranted: (List<GalleryImageInfo>) -> Unit
    ) {
        this.onGranted = onGranted

        if (isFullAccessGranted()) {
            scope.launch {
                val images = getImageFromGallery()
                onGranted(images)
            }
        } else if (isPartialAccessGranted()) {
            // 이미 부분적 권한을 허용한 경우, 전체 권한을 받으려면 사용자가 설정창에 직접 가서 변경하는 방법 밖에 없음
            redirectUserToSetting()
        } else if (isFullAccessGrantedUpToAPI32()) {
            scope.launch {
                val images = getImageFromGallery()
                onGranted(images)
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    readImagePermission.first()
                )
            ) {
                showPermissionExplanation(true) {
                    requestMultiplePermissionLauncher.launch(readImagePermission)
                }
            } else {
                requestMultiplePermissionLauncher.launch(readImagePermission)
            }
        }
    }

    private suspend fun getImageFromGallery(): List<GalleryImageInfo> = withContext(Dispatchers.IO){
        val contentResolver = fragment.requireContext().contentResolver

        val projection = arrayOf(
            Images.Media._ID,
            Images.Media.DISPLAY_NAME,
        )

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            Images.Media.EXTERNAL_CONTENT_URI
        }

        val images = mutableListOf<GalleryImageInfo>()

        contentResolver.query(
            collectionUri,
            projection,
            null,
            null,
            "${Images.Media.DATE_ADDED} DESC",
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collectionUri, cursor.getLong(idColumn))
                val name = cursor.getString(displayNameColumn)

                val image = GalleryImageInfo(uri, name)
                images.add(image)
            }
        }
        return@withContext images
    }

    @Deprecated("callback이 아닌 Coroutine으로 바꿔야 함")
    fun requestPartialImageAccessPermission(
        isImageReselect: Boolean = false,
        onGranted: (List<GalleryImageInfo>) -> Unit,
    ) {
        this.isImageReselect = isImageReselect
        this.onGranted = onGranted

        if (isPartialAccessGranted()) {
            if (isImageReselect) {
                requestMultiplePermissionLauncher.launch(readImagePermission)
            } else {
                scope.launch {
                    val images = getImageFromGallery()
                    onGranted(images)
                }
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    readImagePermission.last()
                )
            ) {
                showPermissionExplanation(true) {
                    requestMultiplePermissionLauncher.launch(readImagePermission)
                }
            } else {
                requestMultiplePermissionLauncher.launch(readImagePermission)
            }
        }
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

    private fun showPermissionExplanation(
        isPossibleToShowPermission: Boolean = false,
        callback: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_title
                )
            )
            .setMessage(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_message
                )
            )
            .setNegativeButton(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_negative
                )
            ) { dialog: DialogInterface, which: Int ->
            }
            .setPositiveButton(
                ContextCompat.getString(
                    fragment.requireContext(),
                    R.string.image_permission_positive
                )
            ) { dialog, which ->
                if (isPossibleToShowPermission) {
                    callback()
                    return@setPositiveButton
                }
                redirectUserToSetting()
            }
            .show()
    }

    private fun redirectUserToSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data =
                Uri.parse("package:" + fragment.requireActivity().baseContext.packageName)
        }
        fragment.startActivity(intent)
    }

    fun isFullImageAccessGranted(): Boolean {
        return isFullAccessGranted() || isFullAccessGrantedUpToAPI32()
    }
}