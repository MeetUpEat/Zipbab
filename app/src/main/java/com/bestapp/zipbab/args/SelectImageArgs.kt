package com.bestapp.zipbab.args

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectImageArgs(
    val uri: Uri,
): Parcelable
