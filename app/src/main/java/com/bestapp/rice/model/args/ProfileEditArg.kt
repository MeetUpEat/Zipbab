package com.bestapp.rice.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileEditArg(
    val userDocumentID: String = "",
    val nickname: String = "",
    val profileImage: String = "",
) : Parcelable
