package com.bestapp.zipbab.ui.profileedit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.args.ProfileEditUi
import com.bestapp.zipbab.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    private val _submitUiState = MutableSharedFlow<SubmitUiState>()
    val submitUiState: SharedFlow<SubmitUiState> = _submitUiState.asSharedFlow()

    private lateinit var originalUrl: String

    fun setUserInfo(profileEditUi: ProfileEditUi) {
        originalUrl = profileEditUi.profileImage

        viewModelScope.launch {
            _uiState.emit(profileEditUi.toUiState())
        }
    }

    fun updateProfileThumbnail(uri: Uri?) {
        viewModelScope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    profileImage = uri?.toString().orEmpty(),
                )
            )
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    nickname = nickname,
                    isNicknameAppliedToView = false,
                )
            )
        }
    }

    // 지금 닉네임과 프로필 변경 함수가 별도로 있다보니, 두 개가 모두 변경된다는 보장을 할 수 없음
    fun submit() {
        viewModelScope.launch {
            _submitUiState.emit(SubmitUiState.Uploading)

            // 닉네임 변경
            runCatching {
                userRepository.updateUserNickname(
                    _uiState.value.userDocumentID,
                    _uiState.value.nickname
                )
            }.onFailure {
                _submitUiState.emit(SubmitUiState.SubmitNicknameFail)
                return@launch
            }

            runCatching {
                if (!_uiState.value.profileImage.startsWith(FIRE_STORAGE_URL)) {
                    userRepository.updateUserProfileImage(
                        _uiState.value.userDocumentID,
                        _uiState.value.profileImage
                    )
                }
                _submitUiState.emit(SubmitUiState.Success)
            }.onFailure {
                _submitUiState.emit(SubmitUiState.SubmitProfileFail)
                return@launch
            }
        }
    }

    fun onRemoveProfileImage() {
        if (_uiState.value.profileImage.isEmpty()) {
            return
        }
        viewModelScope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    profileImage = "",
                )
            )
        }
    }

    companion object {
        private const val FIRE_STORAGE_URL = "https://firebasestorage.googleapis.com"
    }

}