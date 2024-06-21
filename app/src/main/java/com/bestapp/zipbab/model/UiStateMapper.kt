package com.bestapp.zipbab.model

import com.bestapp.zipbab.data.model.remote.FilterResponse
import com.bestapp.zipbab.data.model.remote.MeetingResponse
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.data.model.remote.PlaceLocation
import com.bestapp.zipbab.data.model.remote.PostResponse
import com.bestapp.zipbab.data.model.remote.Review
import com.bestapp.zipbab.data.model.remote.TermInfoResponse
import com.bestapp.zipbab.data.model.remote.UserResponse
import com.bestapp.zipbab.args.FilterArgs
import com.bestapp.zipbab.args.ImageArgs
import com.bestapp.zipbab.args.MeetingArgs
import com.bestapp.zipbab.args.PlaceLocationArgs
import com.bestapp.zipbab.args.ProfileEditArgs
import com.bestapp.zipbab.ui.profile.ProfileUiState
import com.bestapp.zipbab.ui.profileedit.ProfileEditUiState
import com.bestapp.zipbab.ui.profileimageselect.GalleryImageInfo
import com.bestapp.zipbab.ui.profilepostimageselect.model.PostGalleryUiState
import com.bestapp.zipbab.ui.profilepostimageselect.model.SelectedImageUiState

// Data -> UiState

fun FilterResponse.Cost.toUiState() = FilterUiState.CostUiState(
    name = name,
    icon = icon,
    type = type,
)

fun FilterResponse.Food.toUiState() = FilterUiState.FoodUiState(
    icon = icon,
    name = name,
)

fun MeetingResponse.toUiState() = MeetingUiState(
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
    hostTemperature = hostTemperature,
    members = members,
    pendingMembers = pendingMembers,
    attendanceCheck = attendanceCheck,
    activation = activation
)

fun MeetingResponse.toUi() = MeetingArgs(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationArgs = PlaceLocationArgs(
        locationAddress = placeLocation.locationAddress,
        locationLat = placeLocation.locationLat,
        locationLong = placeLocation.locationLong,
    ),
    time = time,
    recruits = recruits,
    description = description,
    mainMenu = mainMenu,
    costValueByPerson = costValueByPerson,
    costTypeByPerson = costTypeByPerson,
    hostUserDocumentID = hostUserDocumentID,
    hostTemperature = hostTemperature,
    members = members,
    pendingMembers = pendingMembers,
    attendanceCheck = attendanceCheck,
    activation = activation,
)

fun PlaceLocation.toUiState() = PlaceLocationUiState(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong,
)

fun PostResponse.toUiState() = PostUiState(
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

fun UserResponse.toUiState() = UserUiState(
    userDocumentID = userDocumentID,
    uuid = uuid,
    nickname = nickname,
    id = id,
    pw = pw,
    profileImage = profileImage,
    temperature = temperature,
    meetingCount = meetingCount,
    notificationUiState = notificationList.map { it.toUiState() },
    meetingReviews = meetingReviews,
    postDocumentIds = posts,
    placeLocationUiState = placeLocation.toUiState(),
)

// UiState -> Data

fun NotificationTypeResponse.toUiState() = when (this) {
    is NotificationTypeResponse.MainResponseNotification -> {
        NotificationUiState.MainNotification(dec = dec, uploadDate = uploadDate)
    }

    is NotificationTypeResponse.UserResponseNotification -> {
        NotificationUiState.UserNotification(dec = dec, uploadDate = uploadDate)
    }
}

fun PlaceLocationUiState.toData() = PlaceLocation(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong
)

fun PostUiState.toData() = PostResponse(
    postDocumentID = postDocumentID,
    images = images,
)


// UiState -> ActionArgs

fun FilterUiState.FoodUiState.toUi() = FilterArgs.FoodArgs(
    icon = icon,
    name = name,
)

fun FilterUiState.CostUiState.toUi() = FilterArgs.CostArgs(
    icon = icon,
    name = name,
    type = type,
)

fun ProfileUiState.toProfileEditUi() = ProfileEditArgs(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
)

fun GalleryImageInfo.toUi() = ImageArgs(
    uri = uri,
    name = name,
)

// UiState -> UiState
fun GalleryImageInfo.toPostGalleryState() = PostGalleryUiState(
    uri = uri,
    name = name,
)

fun PostGalleryUiState.toSelectUiState() = SelectedImageUiState(
    uri = uri,
    name = name,
    order = order,
)

fun SelectedImageUiState.toGalleryUiState() = PostGalleryUiState(
    uri = uri,
    name = name,
    order = order,
)

// Ui -> UiState

fun ProfileEditArgs.toUiState() = ProfileEditUiState(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
)