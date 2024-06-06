package com.bestapp.rice.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bestapp.rice.model.UserUiState

class SignUpViewModel(
    //repository추후 추가예정
): ViewModel() {
    private val _userData = MutableLiveData<UserUiState>()
    val userData : LiveData<UserUiState> = _userData

    fun userDataSave() {
        //repository와 데이터 송수신
    }
}