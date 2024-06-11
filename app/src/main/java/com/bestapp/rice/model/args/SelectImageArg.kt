package com.bestapp.rice.model.args

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectImageArg(
    val uri: Uri,
): Parcelable
