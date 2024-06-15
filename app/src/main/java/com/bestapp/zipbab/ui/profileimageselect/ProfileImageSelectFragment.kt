package com.bestapp.zipbab.ui.profileimageselect

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.databinding.FragmentProfileImageSelectBinding
import com.bestapp.zipbab.model.toUi
import com.bestapp.zipbab.permission.ImagePermissionManager
import com.bestapp.zipbab.permission.ImagePermissionType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileImageSelectFragment : Fragment() {

    private var _binding: FragmentProfileImageSelectBinding? = null
    private val binding: FragmentProfileImageSelectBinding
        get() = _binding!!

    private val imagePermissionManager = ImagePermissionManager(this)
    private val adapter = ProfileImageSelectAdapter {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PROFILE_IMAGE_SELECT_KEY,
            it.toUi()
        )
        if (!findNavController().popBackStack()) {
            requireActivity().finish()
        }
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

        setFragmentResultListener(PROFILE_IMAGE_PERMISSION_TYPE_KEY) { requestKey, bundle ->
            val imagePermissionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(
                    ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY,
                    ImagePermissionType::class.java
                )
            } else {
                bundle.getParcelable(ImagePermissionType.IMAGE_PERMISSION_REQUEST_KEY)
            } ?: return@setFragmentResultListener
            when (imagePermissionType) {
                ImagePermissionType.FULL -> imagePermissionManager.requestFullImageAccessPermission { images: List<GalleryImageInfo> ->
                    adapter.submitList(images)
                }

                ImagePermissionType.PARTIAL -> imagePermissionManager.requestPartialImageAccessPermission(
                    true
                ) { images: List<GalleryImageInfo> ->
                    adapter.submitList(images)
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPermissionManager()
        setRecyclerView()
        setListener()
    }

    private fun setPermissionManager() {
        imagePermissionManager.setScope(viewLifecycleOwner.lifecycleScope)
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

    override fun onResume() {
        super.onResume()

        setPermissionUI()
    }

    private fun setPermissionUI() {
        val isFullImageAccessGranted = imagePermissionManager.isFullImageAccessGranted()

        listOf(
            binding.vPermissionRequestBackground,
            binding.tvPermissionDescription,
            binding.tvRequestPermission
        ).map { view ->
            view.isGone = isFullImageAccessGranted
        }
        if (isFullImageAccessGranted) {
            imagePermissionManager.requestFullImageAccessPermission { images: List<GalleryImageInfo> ->
                adapter.submitList(images)
            }
        } else {
            imagePermissionManager.requestPartialImageAccessPermission { images: List<GalleryImageInfo> ->
                adapter.submitList(images)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        const val PROFILE_IMAGE_PERMISSION_TYPE_KEY = "PROFILE_IMAGE_PERMISSION_TYPE_KEY"
        const val PROFILE_IMAGE_SELECT_KEY = "PROFILE_IMAGE_SELECT_KEY"
    }
}