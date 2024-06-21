package com.bestapp.zipbab.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileEditArgs(
    val userDocumentID: String = "",
    val nickname: String = "",
    val profileImage: String = "",
) : Parcelable
