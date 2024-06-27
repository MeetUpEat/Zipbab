package com.bestapp.zipbab.ui.profileimageselect

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.databinding.FragmentProfileImageSelectBinding
import com.bestapp.zipbab.model.toArgs
import com.bestapp.zipbab.permission.ImagePermissionType
import com.bestapp.zipbab.permission.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileImageSelectFragment : Fragment() {

    private var _binding: FragmentProfileImageSelectBinding? = null
    private val binding: FragmentProfileImageSelectBinding
        get() = _binding!!

    private val permissionManager = PermissionManager(this)

    private val viewModel: ProfileImageSelectViewModel by viewModels()

    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantsInfo ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (grantsInfo[Manifest.permission.READ_MEDIA_IMAGES] == true) {
                    onGranted()
                } else if (grantsInfo[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true) {
                    onGranted()
                } else {
                    permissionManager.showPermissionExplanationDialog()
                }
            } else if (grantsInfo.values.first()) {
                onGranted()
            } else {
                permissionManager.showPermissionExplanationDialog()
            }
        }

    private val adapter = ProfileImageSelectAdapter {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PROFILE_IMAGE_SELECT_KEY,
            it.toArgs()
        )
        if (!findNavController().popBackStack()) {
            requireActivity().finish()
        }
    }

    private fun onGranted() {
        // 권한이 바뀐 경우, 페이징을 갱신해서 이미지를 새롭게 불러온다.
        adapter.refresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileImageSelectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(PROFILE_IMAGE_PERMISSION_TYPE_KEY) { _, bundle ->
            val imagePermissionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(
                    ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY,
                    ImagePermissionType::class.java
                )
            } else {
                bundle.getParcelable(ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY)
            } ?: return@setFragmentResultListener
            when (imagePermissionType) {
                ImagePermissionType.FULL -> permissionManager.requestFullImageAccessPermission(
                    requestMultiplePermissionLauncher
                ) {
                    onGranted()
                }

                ImagePermissionType.PARTIAL -> permissionManager.requestPartialImageAccessPermission(
                    requestMultiplePermissionLauncher,
                    true
                ) {
                    onGranted()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        setListener()
        setObserve()
    }

    private fun setRecyclerView() {
        binding.rvGallery.adapter = adapter
    }

    private fun setListener() {
        /**
         *  빠르게 두 번 누를 경우, fragment manager의 backstack 최상단 fragment가 ModalBottomSheet이기 떄문에 아래와 같은 에러가 발생한다.
         *  그래서 현재 Fragment가 ProfileImageSelectFragment인지 확인하는 조건 문 추가함
         *  java.lang.IllegalArgumentException: Navigation action/destination com.bestapp.zipbab:id/action_profileImageSelectFragment_to_imagePermissionModalBottomSheet cannot be found from the current destination Destination(com.bestapp.zipbab:id/imagePermissionModalBottomSheet) label=ImagePermissionModalBottomSheet
         */
        binding.vPermissionRequestBackground.setOnClickListener {
            if (parentFragmentManager.fragments.last() !is ProfileImageSelectFragment) {
                return@setOnClickListener
            }
            val action =
                ProfileImageSelectFragmentDirections.actionProfileImageSelectFragmentToImagePermissionModalBottomSheet()
            findNavController().navigate(action)
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.items.flowWithLifecycle(lifecycle)
                .collectLatest {
                    adapter.submitData(it)
                }
        }
    }

    override fun onResume() {
        super.onResume()

        setPermissionUI()
    }

    private fun setPermissionUI() {
        val isFullImageAccessGranted = permissionManager.isFullImageAccessGranted()

        listOf(
            binding.vPermissionRequestBackground,
            binding.tvPermissionDescription,
            binding.tvRequestPermission
        ).map { view ->
            view.isGone = isFullImageAccessGranted
        }
        if (isFullImageAccessGranted) {
            permissionManager.requestFullImageAccessPermission(requestMultiplePermissionLauncher) {
                onGranted()
            }
        } else {
            permissionManager.requestPartialImageAccessPermission(
                requestMultiplePermissionLauncher,
                false
            ) {
                onGranted()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        binding.rvGallery.adapter = null

        super.onDestroyView()
    }

    companion object {
        const val PROFILE_IMAGE_PERMISSION_TYPE_KEY = "PROFILE_IMAGE_PERMISSION_TYPE_KEY"
        const val PROFILE_IMAGE_SELECT_KEY = "PROFILE_IMAGE_SELECT_KEY"
    }
}