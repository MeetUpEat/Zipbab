package com.bestapp.zipbab.ui.mettinginfo

data class MemberInfo(
    val userDocumentID: String,
    val nickname: String = "",
    val profileImage: String = "",
    val isHost: Boolean = false,
)
