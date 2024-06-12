package com.bestapp.rice.service

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.ui.profilepostimageselect.model.SubmitInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UploadService : LifecycleService() {

    private lateinit var userRepository: UserRepository

    @Inject
    fun injectUserRepository(userRepository: UserRepository) {
        this.userRepository = userRepository
    }


    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        const val UPLOADING_INFO_KEY = "UPLOADING_INFO_KEY"
    }
}