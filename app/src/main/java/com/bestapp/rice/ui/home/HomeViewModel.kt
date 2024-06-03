package com.bestapp.rice.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.Filter
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import kotlinx.coroutines.flow.MutableSharedFlow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch


class HomeViewModel(
    private val appSettingRepository: AppSettingRepository,
    private val categoryRepository: CategoryRepository,
    private val meetingRepositoryImp: MeetingRepository
) : ViewModel() {

    private val _goNavigate = MutableSharedFlow<MoveNavigate>(replay = 0)
    val goNavigate: SharedFlow<MoveNavigate>
        get() = _goNavigate

    private val _foodCategory = MutableStateFlow<List<FilterUiState.FoodUiState>>(emptyList())
    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>>
        get() = _foodCategory

    private val _costCategory = MutableStateFlow<List<FilterUiState.CostUiState>>(emptyList())
    val costCategory: StateFlow<List<FilterUiState.CostUiState>>
        get() = _costCategory

    private val _enterMeeting = MutableStateFlow<List<MeetingUiState>>(emptyList())
    val enterMeeting: StateFlow<List<MeetingUiState>>
        get() = _enterMeeting

    private val _isLogin = MutableSharedFlow<Boolean>(replay = 1)
    val isLogin: SharedFlow<Boolean>
        get() = _isLogin

    fun checkLogin() {

        viewModelScope.launch {
            runCatching {
                appSettingRepository.getUserInfo().userDocumentID.isNotEmpty()
            }.onSuccess {
                _isLogin.emit(it)
            }
        }
    }

    fun goNavigate() {

        viewModelScope.launch {
            runCatching {
                appSettingRepository.getUserInfo().userDocumentID.isNotEmpty()
            }.onSuccess {
                if (it) {
                    _goNavigate.emit(MoveNavigate.GOCREATMEET)
                } else {
                    _goNavigate.emit(MoveNavigate.GOLOGIN)
                }
            }
        }
    }

    fun getFoodCategory() {
        viewModelScope.launch {
            runCatching {
                categoryRepository.getFoodCategory()
            }.onSuccess {
                val foodUiStateList = it.map { filter ->
                    FilterUiState.FoodUiState.createFrom(filter as Filter.Food)
                }
                _foodCategory.value = foodUiStateList
            }
        }
    }

    fun getCostCategory() {
        viewModelScope.launch {
            runCatching {
                categoryRepository.getCostCategory()
            }.onSuccess {
                val costUiStateList = it.map { filter ->
                    FilterUiState.CostUiState.createFrom(filter as Filter.Cost)
                }
                _costCategory.value = costUiStateList
            }
        }
    }

    fun getMeetingByUserDocumentID() {
        viewModelScope.launch {
            runCatching {
                meetingRepositoryImp.getMeetingByUserDocumentID()
            }.onSuccess {
                val meetingUiStateList = it.map { meeting ->
                    MeetingUiState.createFrom(meeting)
                }
                _enterMeeting.value = meetingUiStateList
            }
        }
    }
}