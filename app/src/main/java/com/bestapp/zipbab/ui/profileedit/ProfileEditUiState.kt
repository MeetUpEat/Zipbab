package com.bestapp.zipbab.ui.profileedit

data class ProfileEditUiState(
    val userDocumentID: String = "",
    val nickname: String = "",
    val profileImage: String = "",
    val isNicknameAppliedToView: Boolean = true,
)
