package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUiState(
    val userDocumentID: String,
    val nickname: String,
    val id: String,
    val pw: String,
    val profileImage: String,
    val temperature: Double,
    val meetingCount: Int,
    val postUiStates: List<PostUiState>,
    val placeLocationUiState: PlaceLocationUiState,
) : Parcelable {

    fun toData() = User(
        userDocumentID = this.userDocumentID,
        nickname = this.nickname,
        id = this.id,
        pw = this.pw,
        profileImage = this.profileImage,
        temperature = this.temperature,
        meetingCount = this.meetingCount,
        posts = this.postUiStates.map {
            it.toData()
        },
        placeLocation = this.placeLocationUiState.toData(),
    )

    companion object {

        val Empty = UserUiState(
            userDocumentID = "",
            nickname = "",
            id = "",
            pw = "",
            profileImage = "",
            temperature = 0.0,
            meetingCount = 0,
            postUiStates = listOf(),
            placeLocationUiState = PlaceLocationUiState(
                locationAddress = "",
                locationLat = "",
                locationLong = ""
            )
        )

        fun createFrom(user: User) = UserUiState(
            userDocumentID = user.userDocumentID,
            nickname = user.nickname,
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
