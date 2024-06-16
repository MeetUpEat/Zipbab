package com.bestapp.zipbab.ui.profile

import com.bestapp.zipbab.model.PostUiState

data class ProfileUiState(
    val userDocumentID: String = "",
    val nickname: String = "",
    val profileImage: String = "",
    val temperature: Double = 0.0,
    val meetingCount: Int = 0,
    val postUiStates: List<PostUiState> = listOf(),
    val isSelfProfile: Boolean = false,
    val isProfileClicked: Boolean = false,
)
