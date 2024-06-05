package com.bestapp.rice.ui.profileimageselect.permission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.bestapp.rice.databinding.PermissionModalBottomSheetContentBinding
import com.bestapp.rice.ui.profileimageselect.ProfileImageSelectFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImagePermissionModalBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PermissionModalBottomSheetContentBinding? = null
    private val binding: PermissionModalBottomSheetContentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PermissionModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(binding.tvRequestAllGrant, binding.vRequestAllGrant).map {
            it.setOnClickListener {
                setFragmentResult(
                    ProfileImageSelectFragment.PROFILE_IMAGE_PERMISSION_TYPE_KEY,
                    bundleOf(ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY to ImagePermissionType.FULL)
                )
                this.dismiss()
            }
        }
        listOf(binding.tvRequestPartialPermission, binding.vRequestPartialPermission).map {
            it.setOnClickListener {
                setFragmentResult(
                    ProfileImageSelectFragment.PROFILE_IMAGE_PERMISSION_TYPE_KEY,
                    bundleOf(ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY to ImagePermissionType.PARTIAL)
                )
                this.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "PermissionModalBottomSheet"
    }
}