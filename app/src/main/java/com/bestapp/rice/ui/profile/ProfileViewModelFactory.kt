package com.bestapp.rice.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.data.repository.UserRepositoryImpl

class ProfileViewModelFactory : ViewModelProvider.Factory {

    private val userRepository: UserRepository = UserRepositoryImpl()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ProfileViewModel::class.java -> ProfileViewModel(userRepository)
            else -> throw IllegalArgumentException()
        } as T
    }
}