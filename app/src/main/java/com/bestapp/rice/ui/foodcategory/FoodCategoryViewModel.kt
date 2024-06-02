package com.bestapp.rice.ui.foodcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.Filter
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodCategoryViewModel(
    private val meetingRepository: MeetingRepository,
    private val categoryRepository: CategoryRepository,
    private val appSettingRepository: AppSettingRepository
) : ViewModel() {

    private val _meetingList = MutableSharedFlow<List<MeetingUiState>>(replay = 0)
    val meetingList: SharedFlow<List<MeetingUiState>>
        get() = _meetingList


    private lateinit var _foodCategory: MutableStateFlow<List<FilterUiState.FoodUiState>>
    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>>
        get() = _foodCategory

    private val _goMeetingNavi = MutableSharedFlow<GoMeetingNavi>(replay = 0)
    val goMeetingNavi: SharedFlow<GoMeetingNavi>
        get() = _goMeetingNavi

    private var _meetingDocumentID = ""
    val meetingDocumentID : String
        get() = _meetingDocumentID

    init {
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


    fun getFoodMeeting(mainMenu: String) {
        viewModelScope.launch {
            runCatching {
                meetingRepository.getFoodMeeting(mainMenu)
            }.onSuccess {
                val meetingUiStateList = it.map { meeting ->
                    MeetingUiState.createFrom(meeting)
                }
                _meetingList.emit(meetingUiStateList)
            }
        }
    }


    fun goMeeting(meetingUiState: MeetingUiState){
        this._meetingDocumentID = meetingUiState.meetingDocumentID
        viewModelScope.launch {
            runCatching {
                appSettingRepository.getUserInfo()
            }.onSuccess {
                if(UserUiState.createFrom(it).userDocumentID == meetingUiState.host){
                    _goMeetingNavi.emit(GoMeetingNavi.GO_MEETING_MANAGEMENT)
                }else{
                    _goMeetingNavi.emit(GoMeetingNavi.GO_MEETING_INFO)
                }
            }
        }
    }
}