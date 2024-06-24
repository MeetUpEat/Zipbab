package com.bestapp.zipbab.ui.recruitment

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.MeetingResponse
import com.bestapp.zipbab.data.model.remote.UserResponse
import com.bestapp.zipbab.data.model.remote.kakaomap.SearchLocationResponse
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.SearchLocationRepository
import com.bestapp.zipbab.data.repository.StorageRepository
import com.bestapp.zipbab.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitmentViewModel @Inject constructor (
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val searchLocationRepository: SearchLocationRepository,
    private val storageRepository: StorageRepository
): ViewModel() {
    private val _recruit = MutableLiveData<Boolean>()
    val recruit : LiveData<Boolean> = _recruit

    fun registerMeeting(meetingResponse: MeetingResponse) = viewModelScope.launch {
        val result = meetingRepository.createMeeting(meetingResponse)
        _recruit.value = result
    }

    private val _hostInfo = MutableLiveData<UserResponse>()
    val hostInfo : LiveData<UserResponse> = _hostInfo

    fun getHostInfo(userDocumentId: String) = viewModelScope.launch {
        val result = userRepository.getUser(userDocumentId)
        _hostInfo.value = result
    }

    fun getDocumentId() = viewModelScope.launch {
        appSettingRepository.userPreferencesFlow.collect {
            getHostInfo(it.ifEmpty { "" })
            /*if(it.isEmpty()) {
                //_getDocumentId.value  = ""
                getHostInfo("")
            } else {
                //_getDocumentId.value = it
                getHostInfo(it)
            }*/
        }
    }

    private val _location = MutableLiveData<SearchLocationResponse>()
    val location : LiveData<SearchLocationResponse> = _location

    fun getLocation(query: String, analyzeType: String) = viewModelScope.launch {
        val result = searchLocationRepository.convertLocation(query, analyzeType)
        _location.value = result
    }

    private val _imageTrans = MutableLiveData<String>()
    val imageTrans : LiveData<String> = _imageTrans

    fun getImageTrans(imageUri: Uri) = viewModelScope.launch {
        val result = storageRepository.uploadImage(imageUri)
        _imageTrans.value = result
    }
}