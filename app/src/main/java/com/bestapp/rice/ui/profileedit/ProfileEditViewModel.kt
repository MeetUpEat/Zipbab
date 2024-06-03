package com.bestapp.rice.ui.profileedit

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileEditViewModel(
    private val userRepository: UserRepository,
    private val bitmapConverter: BitmapConverter,
): ViewModel() {

    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Empty)
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    private val _message = MutableSharedFlow<ProfileEditMessage>()
    val message: SharedFlow<ProfileEditMessage> = _message.asSharedFlow()

    private val _isProfileUpdateSuccessful = MutableSharedFlow<Boolean>()
    val isProfileUpdateSuccessful: SharedFlow<Boolean> = _isProfileUpdateSuccessful.asSharedFlow()

    fun setUserInfo(userUiState: UserUiState) {
        viewModelScope.launch {
            _userUiState.emit(userUiState)
        }
    }

    fun updateProfileThumbnail(uri: Uri?) {
        viewModelScope.launch {
            _userUiState.emit(_userUiState.value.copy(profileImage = uri?.toString() ?: ""))
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            _userUiState.emit(_userUiState.value.copy(nickName = nickname))
        }
    }

    // 지금 닉네임과 프로필 변경 함수가 별도로 있다보니, 두 개가 모두 변경된다는 보장을 할 수 없음
    fun submit() {
        val userDocumentId = _userUiState.value.userDocumentID
        val originUrl = _userUiState.value.profileImage

        viewModelScope.launch {
            // 닉네임 변경
            runCatching {
                userRepository.updateUserNickname(userDocumentId, _userUiState.value.nickName)
            }.onFailure {
                _message.emit(ProfileEditMessage.EDIT_NICKNAME_FAIL)
                return@launch
            }

            runCatching {
                // 사용자가 프로필을 변경하지 않아서, 로컬 경로를 나타내는 uri가 아닌 fireStore의 url이면 pass
                val profileImageUrl = if (originUrl.startsWith(FIRE_STORAGE_URL)) {
                    originUrl
                } else {
                    val bitmap = bitmapConverter.convert(originUrl)
                    userRepository.convertImages(userDocumentId, listOf(bitmap)).first()
                }
                userRepository.updateUserProfileImage(userDocumentId, profileImageUrl)
                _isProfileUpdateSuccessful.emit(true)
            }.onFailure {
                _message.emit(ProfileEditMessage.EDIT_PROFILE_IMAGE_FAIL)
                return@launch
            }
        }
    }

    companion object {
        private const val FIRE_STORAGE_URL = "https://firebasestorage.googleapis.com"
    }

}