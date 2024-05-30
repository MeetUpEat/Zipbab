package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUiState(
    val userDocumentID: String,
    val nickName: String,
    val id: String,
    val pw: String,
    val profileImage: String,
    val temperature: Double,
    val meetingCount: Int,
    val postUiStates: List<PostUiState>,
    val placeLocationUiState: PlaceLocationUiState,
) : Parcelable {

    companion object {

        fun createFrom(user: User) = UserUiState(
            userDocumentID = user.userDocumentID,
            nickName = user.nickName,
            id = user.id,
            pw = user.pw,
            profileImage = user.profileImage,
            temperature = user.temperature,
            meetingCount = user.meetingCount,
            postUiStates = user.posts.map {
                PostUiState.createFrom(it)
            },
            placeLocationUiState = PlaceLocationUiState.createFrom(user.placeLocation),
        )
    }
}
