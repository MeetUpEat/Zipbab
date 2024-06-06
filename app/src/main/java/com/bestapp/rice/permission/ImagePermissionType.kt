package com.bestapp.rice.permission

enum class ImagePermissionType {
    FULL, PARTIAL;

    companion object {
        const val IMAGE_PERMISSION_REQUEST_KEY = "IMAGE_PERMISSION_REQUEST_KEY"
    }
}