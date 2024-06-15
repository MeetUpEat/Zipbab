package com.bestapp.zipbab.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileEditUi(
    val userDocumentID: String = "",
    val nickname: String = "",
    val profileImage: String = "",
) : Parcelable
