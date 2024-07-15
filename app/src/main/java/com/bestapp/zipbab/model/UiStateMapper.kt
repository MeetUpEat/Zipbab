package com.bestapp.zipbab.model

import com.bestapp.zipbab.data.model.UploadStateEntity
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
import com.bestapp.zipbab.args.ImagePostSubmitArgs
import com.bestapp.zipbab.args.MeetingArgs
import com.bestapp.zipbab.args.PlaceLocationArgs
import com.bestapp.zipbab.args.ProfileEditArgs
import com.bestapp.zipbab.args.SelectImageArgs
import com.bestapp.zipbab.ui.profile.ProfileUiState
import com.bestapp.zipbab.ui.profileedit.ProfileEditUiState
import com.bestapp.zipbab.data.model.local.GalleryImageInfo
import com.bestapp.zipbab.data.model.local.SignOutEntity
import com.bestapp.zipbab.data.model.remote.LoginResponse
import com.bestapp.zipbab.data.model.remote.SignUpResponse
import com.bestapp.zipbab.ui.mettinginfo.MemberInfo
import com.bestapp.zipbab.ui.profilepostimageselect.model.PostGalleryUiState
import com.bestapp.zipbab.ui.profilepostimageselect.model.SelectedImageUiState
import com.bestapp.zipbab.ui.profilepostimageselect.model.SubmitInfo
import com.bestapp.zipbab.ui.signup.SignUpState

// Data -> UiState

fun SignOutEntity.toUiState(): SignOutState {
    return when (this) {
        SignOutEntity.Fail -> SignOutState.Fail
        SignOutEntity.IsNotAllowed -> SignOutState.IsNotAllowed
        SignOutEntity.Success -> SignOutState.Success
    }
}

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

fun MeetingResponse.toArgs() = MeetingArgs(
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
    state = UploadState.Default(
        tempPostDocumentID = postDocumentID
    ),
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
    nickname = nickname,
    id = id,
    pw = pw,
    profileImage = profileImage,
    temperature = temperature,
    meetingCount = meetingCount,
    notificationUiState = notifications.map { it.toUiState() },
    meetingReviews = meetingReviews,
    postDocumentIds = posts,
    placeLocationUiState = placeLocation.toUiState(),
)

fun UploadStateEntity.toArgs(): UploadState {
    return when (this) {
        is UploadStateEntity.Fail -> UploadState.Fail(
            tempPostDocumentID = tempPostDocumentID
        )

        is UploadStateEntity.Pending -> UploadState.Pending(
            tempPostDocumentID = tempPostDocumentID
        )

        is UploadStateEntity.ProcessImage -> UploadState.InProgress(
            tempPostDocumentID = tempPostDocumentID,
            currentProgressOrder = currentProgressOrder,
            maxOrder = maxOrder,
        )

        is UploadStateEntity.ProcessPost -> UploadState.ProcessPost(
            tempPostDocumentID = tempPostDocumentID,
        )

        is UploadStateEntity.SuccessPost -> UploadState.SuccessPost(
            tempPostDocumentID = tempPostDocumentID,
            postDocumentID = postDocumentID,
        )
    }
}

fun LoginResponse.toUi(): LoginResult {
    return when (this) {
        LoginResponse.Fail -> LoginResult.Fail
        is LoginResponse.Success -> LoginResult.Success(this.userDocumentID)
    }
}

fun SignUpResponse.toUi(): SignUpState {
    return when (this) {
        SignUpResponse.DuplicateEmail -> SignUpState.DuplicateEmail
        SignUpResponse.Fail -> SignUpState.Fail
        is SignUpResponse.Success -> SignUpState.Success(this.userDocumentID)
    }
}

// UiState -> Data

fun NotificationTypeResponse.toUiState(): NotificationUiState.UserNotification {
    return NotificationUiState.UserNotification(dec = "", uploadDate = uploadDate)
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

fun FilterUiState.FoodUiState.toArgs() = FilterArgs.FoodArgs(
    icon = icon,
    name = name,
)

fun FilterUiState.CostUiState.toArgs() = FilterArgs.CostArgs(
    icon = icon,
    name = name,
    type = type,
)

fun ProfileUiState.toProfileEditArgs() = ProfileEditArgs(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
)

fun GalleryImageInfo.toArgs() = ImageArgs(
    uri = uri,
    name = name,
)

fun SelectedImageUiState.toArgs() = SelectImageArgs(
    uri = uri,
)

fun SubmitInfo.toArgs() = ImagePostSubmitArgs(
    userDocumentID = userDocumentID,
    images = images,
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

fun UserUiState.toMemberInfo(isHost: Boolean) = MemberInfo(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
    isHost = isHost,
)

// Args -> UiState

fun ProfileEditArgs.toUiState() = ProfileEditUiState(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
)