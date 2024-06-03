package com.bestapp.rice.ui.foodcategory

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
    private val appSettingRepository: AppSettingRepository,
//    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _meetingList = MutableSharedFlow<List<MeetingUiState>>(replay = 1)
    val meetingList: SharedFlow<List<MeetingUiState>>
        get() = _meetingList
//
//    private val _foodCategory = MutableStateFlow<List<FilterUiState.FoodUiState>>(emptyList())
//    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>>
//        get() = _foodCategory

//    private lateinit var _foodCategory: MutableStateFlow<List<FilterUiState.FoodUiState>>
//    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>>
//        get() = _foodCategory


    private val _foodCategory = MutableStateFlow<List<FilterUiState.FoodUiState>>(emptyList())
    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>> = _foodCategory

    private val _goMeetingNavi = MutableSharedFlow<GoMeetingNavi>(replay = 0)
    val goMeetingNavi: SharedFlow<GoMeetingNavi>
        get() = _goMeetingNavi

    private var _meetingDocumentID = ""
    val meetingDocumentID : String
        get() = _meetingDocumentID


    private var _selectMenu = ""

    init {
//        Log.e("cyc","init")
//        Log.e("cyc","_selectMenu-->${_selectMenu}")

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
//        savedStateHandle.get<FilterUiState.FoodUiState>("foodCategory")?.let {
//            getFoodMeeting(it.name)
//        }
    }


//    init {
//        viewModelScope.launch {
//            runCatching {
//                categoryRepository.getFoodCategory()
//            }.onSuccess {
//                val foodUiStateList = it.map { filter ->
//                    FilterUiState.FoodUiState.createFrom(filter as Filter.Food)
//                }
//                _foodCategory = MutableStateFlow(foodUiStateList)
//            }
//        }
//    }


    fun getFoodMeeting(mainMenu: String) {
        viewModelScope.launch {
            runCatching {
                meetingRepository.getFoodMeeting(mainMenu)
            }.onSuccess {
                val meetingUiStateList = it.map{meeting ->
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

    fun setSelectMenu(menu: String){
        _selectMenu = menu
        Log.e("cyc","아래_selectMenu-->${_selectMenu}")

    }

    fun getSelectMenu(): String{
        return _selectMenu
    }
}