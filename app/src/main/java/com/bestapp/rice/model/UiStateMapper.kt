package com.bestapp.rice.model

import com.bestapp.rice.data.model.remote.Filter
import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.TermInfoResponse
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.model.args.FilterUi
import com.bestapp.rice.model.args.PlaceLocationUi
import com.bestapp.rice.model.args.PostUi
import com.bestapp.rice.model.args.ProfileEditUi
import com.bestapp.rice.model.args.UserActionUi
import com.bestapp.rice.ui.profile.ProfileUiState

// Data -> UiState

fun Filter.Cost.toUiState() = FilterUiState.CostUiState(
    name = name,
    icon = icon,
    type = type,
)

fun Filter.Food.toUiState() = FilterUiState.FoodUiState(
    icon = icon,
    name = name,
)

fun Meeting.toUiState() = MeetingUiState(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUiState = placeLocation.toUiState(),
    time = time,
    recruits = recruits,
    description = description,
    mainMenu = mainMenu,
    costValueByPerson = costValueByPerson,
    costTypeByPerson = costTypeByPerson,
    hostUserDocumentID = hostUserDocumentID,
    members = members,
    pendingMembers = pendingMembers,
    attendanceCheck = attendanceCheck,
    activation = activation
)

fun PlaceLocation.toUiState() = PlaceLocationUiState(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong,
)

fun Post.toUiState() = PostUiState(
    postDocumentID = postDocumentID,
    images = images,
)

fun Review.toUiState() = ReviewUiState(
    id = id,
    votingPoint = votingPoint,
)

fun TermInfoResponse.toUiState() = TermInfoState(
    id = id,
    content = content,
    date = date,
)

fun User.toUiState() = UserUiState(
    userDocumentID = userDocumentID,
    nickname = nickname,
    id = id,
    pw = pw,
    profileImage = profileImage,
    temperature = temperature,
    meetingCount = meetingCount,
    postUiStates = posts.map { it.toUiState() },
    placeLocationUiState = placeLocation.toUiState(),
)

// UiState -> Data

fun PlaceLocationUiState.toData() = PlaceLocation(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong
)

fun PostUiState.toData() = Post(
    postDocumentID = postDocumentID,
    images = images,
)


// UiState -> ActionArgs

fun UserUiState.toUi() = UserActionUi(
    userDocumentID = userDocumentID,
    nickname = nickname,
    id = id,
    pw = pw,
    profileImage = profileImage,
    temperature = temperature,
    meetingCount = meetingCount,
    postUis = postUiStates.map { it.toUi() },
    placeLocationUi = placeLocationUiState.toUi(),
)

fun PlaceLocationUiState.toUi() = PlaceLocationUi(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong,
)

fun PostUiState.toUi() = PostUi(
    postDocumentID = postDocumentID,
    images = images,
)

fun FilterUiState.FoodUiState.toUi() = FilterUi.FoodUi(
    icon = icon,
    name = name,
)

fun FilterUiState.CostUiState.toUi() = FilterUi.CostUi(
    icon = icon,
    name = name,
    type = type,
)

fun ProfileUiState.toProfileEditUi() = ProfileEditUi(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
)