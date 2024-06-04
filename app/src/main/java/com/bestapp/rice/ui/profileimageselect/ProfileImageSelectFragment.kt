package com.bestapp.rice.ui.profileimageselect

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import com.bestapp.rice.databinding.FragmentProfileImageSelectBinding
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.profileimageselect.permission.ImagePermissionManager
import com.bestapp.rice.ui.profileimageselect.permission.ImagePermissionModalBottomSheet
import com.bestapp.rice.ui.profileimageselect.permission.ImagePermissionType

class ProfileImageSelectFragment :
    BaseFragment<FragmentProfileImageSelectBinding>(FragmentProfileImageSelectBinding::inflate) {

    private val modalBottomSheet = ImagePermissionModalBottomSheet()

    private val imagePermissionManager = ImagePermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(PROFILE_IMAGE_SELECT_RESULT_KEY) { requestKey, bundle ->
            val imagePermissionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY, ImagePermissionType::class.java)
            } else {
                bundle.getParcelable(ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY)
            } ?: return@setFragmentResultListener
            when (imagePermissionType) {
                ImagePermissionType.FULL -> imagePermissionManager.requestFullImageAccessPermission {

                }
                ImagePermissionType.PARTIAL -> imagePermissionManager.requestPartialImageAccessPermission(true) {

                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
    }

    private fun setListener() {
        binding.vPermissionRequestBackground.setOnClickListener {
            modalBottomSheet.show(parentFragmentManager, ImagePermissionModalBottomSheet.TAG)
        }
    }

    override fun onResume() {
        super.onResume()

        setPermissionUI()
    }

    private fun setPermissionUI() {
        val visibility = if (imagePermissionManager.isFullImageAccessGranted()) View.GONE else View.VISIBLE

        listOf(binding.vPermissionRequestBackground, binding.tvPermissionDescription, binding.tvRequestPermission).map { view ->
            view.visibility = visibility
        }
    }

    companion object {
        const val PROFILE_IMAGE_SELECT_RESULT_KEY = "PROFILE_IMAGE_SELECT_RESULT_KEY"
    }
}