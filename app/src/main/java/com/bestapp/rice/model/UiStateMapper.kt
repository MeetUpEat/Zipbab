package com.bestapp.rice.model

import com.bestapp.rice.data.model.remote.Filter
import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.TermInfoResponse
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.model.args.PlaceLocationArg
import com.bestapp.rice.model.args.PostArg
import com.bestapp.rice.model.args.UserActionArg

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
    host = host,
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

fun UserUiState.toArg() = UserActionArg(
    userDocumentID = userDocumentID,
    nickname = nickname,
    id = id,
    pw = pw,
    profileImage = profileImage,
    temperature = temperature,
    meetingCount = meetingCount,
    postArgs = postUiStates.map { it.toArg() },
    placeLocationArg = placeLocationUiState.toArg(),
)

fun PlaceLocationUiState.toArg() = PlaceLocationArg(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong,
)

fun PostUiState.toArg() = PostArg(
    postDocumentID = postDocumentID,
    images = images,
)