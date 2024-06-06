package com.bestapp.rice.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserActionArg(
    val userDocumentID: String = "",
    val nickname: String,
    val id: String,
    val pw: String,
    val profileImage: String,
    val temperature: Double,
    val meetingCount: Int,
    val postArgs: List<PostArg>,
    val placeLocationArg: PlaceLocationArg,
): Parcelable
