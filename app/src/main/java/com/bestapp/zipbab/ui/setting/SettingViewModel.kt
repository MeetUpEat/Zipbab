package com.bestapp.zipbab.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.local.SignOutEntity
import com.bestapp.zipbab.data.model.remote.Privacy
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.UserUiState
import com.bestapp.zipbab.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val userUiState: StateFlow<UserUiState> = appSettingRepository.userPreferencesFlow
        .map { userDocumentID ->
            if (userDocumentID.isBlank()) {
                UserUiState()
            } else {
                userRepository.getUser(userDocumentID).toUiState()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserUiState(),
        )

    private val _navActionIntent = MutableStateFlow<NavActionIntent>(NavActionIntent.Default)
    val navActionIntent: StateFlow<NavActionIntent> = _navActionIntent.asStateFlow()

    private val _actionIntent = MutableStateFlow<ActionIntent>(ActionIntent.Default)
    val actionIntent: StateFlow<ActionIntent> = _actionIntent.asStateFlow()

    private val _message = MutableSharedFlow<SettingMessage>()
    val message: SharedFlow<SettingMessage> = _message.asSharedFlow()

    private val requestDeleteUrl = MutableStateFlow("")

    private val _requestPrivacyUrl = MutableStateFlow(Privacy())
    val requestPrivacyUrl: StateFlow<Privacy> = _requestPrivacyUrl.asStateFlow()

    private val _requestLocationPolicyUrl = MutableStateFlow(Privacy())
    val requestLocationPolicyUrl: StateFlow<Privacy> = _requestLocationPolicyUrl.asStateFlow()

    init {
        viewModelScope.launch {
            requestDeleteUrl.emit(appSettingRepository.getDeleteRequestUrl())
            _requestPrivacyUrl.emit(appSettingRepository.getPrivacyInfo())
            _requestLocationPolicyUrl.emit(appSettingRepository.getLocationPolicyInfo())
        }
    }

    fun handleAction(settingIntent: SettingIntent) {
        when (settingIntent) {
            SettingIntent.Default -> {
                _navActionIntent.value = NavActionIntent.Default
            }

            SettingIntent.SignOut -> signOut()
            SettingIntent.Logout -> logout()
            SettingIntent.Login -> {
                _navActionIntent.value = NavActionIntent.Login("")
            }

            SettingIntent.Profile -> {
                _navActionIntent.value = NavActionIntent.Profile(userUiState.value.userDocumentID)
            }

            SettingIntent.Meeting -> {
                _navActionIntent.value = NavActionIntent.Meeting
            }

            SettingIntent.SignUp -> {
                _navActionIntent.value = NavActionIntent.SignUp
            }

            SettingIntent.RequestDelete -> {
                viewModelScope.launch {
                    _actionIntent.emit(ActionIntent.DirectToRequestDelete(
                        url = requestDeleteUrl.value
                    ))
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            runCatching {
                appSettingRepository.removeUserDocumentId()
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            runCatching {
                val userDocumentID = userUiState.firstOrNull()?.userDocumentID ?: return@runCatching
                val signOutState = userRepository.signOutUser(userDocumentID)
                when (signOutState) {
                    SignOutEntity.Fail -> _message.emit(SettingMessage.SignOutFail)
                    SignOutEntity.IsNotAllowed -> _message.emit(SettingMessage.SignOutIsNotAllowed)
                    SignOutEntity.Success -> {
                        appSettingRepository.removeUserDocumentId()
                        _message.emit(SettingMessage.SingOutSuccess)
                    }
                }
            }
        }
    }
}