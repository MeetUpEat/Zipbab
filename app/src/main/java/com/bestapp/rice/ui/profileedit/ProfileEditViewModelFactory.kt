package com.bestapp.rice.ui.profileedit

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.data.repository.UserRepositoryImpl

class ProfileEditViewModelFactory(contentResolver: ContentResolver): ViewModelProvider.Factory {

    private val userRepository: UserRepository = UserRepositoryImpl()
    private val bitmapConverter = BitmapConverter(contentResolver)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass) {
            ProfileEditViewModel::class.java -> ProfileEditViewModel(userRepository, bitmapConverter)
            else -> throw IllegalArgumentException()
        } as T
    }
}