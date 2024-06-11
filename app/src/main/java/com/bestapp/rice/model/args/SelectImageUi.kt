package com.bestapp.rice.model.args

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectImageUi(
    val uri: Uri,
): Parcelable
