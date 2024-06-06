package com.bestapp.rice.ui.profileedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.network.FirebaseClient
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.data.repository.UserRepositoryImpl

class ProfileEditViewModelFactory : ViewModelProvider.Factory {

    private val userRepository: UserRepository = UserRepositoryImpl(FirebaseClient.userStoreService)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ProfileEditViewModel::class.java -> ProfileEditViewModel(userRepository)
            else -> throw IllegalArgumentException()
        } as T
    }
}