package com.bestapp.zipbab.args

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageArgs(
    val uri: Uri,
    val name: String,
) : Parcelable
