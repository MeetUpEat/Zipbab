package com.bestapp.rice.ui.profilepostimageselect

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentProfilePostImageSelectBinding
import com.bestapp.rice.permission.ImagePermissionManager
import com.bestapp.rice.permission.ImagePermissionType
import com.bestapp.rice.service.UploadService
import com.bestapp.rice.ui.profile.ProfileFragmentArgs
import com.bestapp.rice.ui.profileimageselect.GalleryImageInfo
import com.bestapp.rice.ui.profileimageselect.ProfileImageSelectFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePostImageSelectFragment : Fragment() {

    private var _binding: FragmentProfilePostImageSelectBinding? = null
    private val binding: FragmentProfilePostImageSelectBinding
        get() = _binding!!

    private val selectedImageAdapter = SelectedImageAdapter {
        viewModel.unselect(it)
    }
    private val galleryAdapter = PostGalleryAdapter {
        viewModel.reverseImageSelecting(it)
    }

    private val args: ProfileFragmentArgs by navArgs()

    private val viewModel: PostImageSelectViewModel by viewModels()
    private val imagePermissionManager = ImagePermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(ProfileImageSelectFragment.PROFILE_IMAGE_PERMISSION_TYPE_KEY) { requestKey, bundle ->
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
                    viewModel.updateGalleryImages(images)
                }

                ImagePermissionType.PARTIAL -> imagePermissionManager.requestPartialImageAccessPermission(
                    true
                ) { images: List<GalleryImageInfo> ->
                    viewModel.updateGalleryImages(images)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,

        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePostImageSelectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        setPermissionManager()
        setListener()
        setObserve()
    }

    private fun setRecyclerView() {
        binding.rvGallery.adapter = galleryAdapter
        binding.rvSelectedImage.adapter = selectedImageAdapter
    }

    private fun setPermissionManager() {
        imagePermissionManager.setScope(viewLifecycleOwner.lifecycleScope)
    }

    private fun setListener() {
        binding.vPermissionRequestBackground.setOnClickListener {
            if (parentFragmentManager.fragments.last() !is ProfilePostImageSelectFragment) {
                return@setOnClickListener
            }
            val action =
                ProfilePostImageSelectFragmentDirections.actionProfilePostImageSelectFragmentToImagePermissionModalBottomSheet()
            findNavController().navigate(action)
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
        binding.mt.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.done -> {
                    // 아이템이 한 개 이상 클릭된 경우에만 완료 버튼을 누를 수 있어야 한다.
                    if (selectedImageAdapter.itemCount < MIN_SELECTED_ITEM) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.post_image_select_min_select_item),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnMenuItemClickListener true
                    }
                    viewModel.submit(args.userDocumentID)
                    true
                }

                else -> false
            }
        }
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.galleryImageStates.flowWithLifecycle(lifecycle)
                .collectLatest { states ->
                    galleryAdapter.submitList(states)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedImageStatesFlow.flowWithLifecycle(lifecycle)
                .collectLatest { states ->
                    selectedImageAdapter.submitList(states)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.submitInfo.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    requireActivity().startService(Intent(requireContext(), UploadService::class.java).apply {
                        putExtra(UploadService.UPLOADING_INFO_KEY, state.toInfo())
                    })
                    Toast.makeText(requireContext(), getString(R.string.message_when_uploading_image_post_start), Toast.LENGTH_SHORT).show()
                    if (!findNavController().popBackStack()) {
                        requireActivity().finish()
                    }
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
        if (imagePermissionManager.isFullImageAccessGranted()) {
            imagePermissionManager.requestFullImageAccessPermission { images: List<GalleryImageInfo> ->
                viewModel.updateGalleryImages(images)
            }
        } else {
            imagePermissionManager.requestPartialImageAccessPermission { images: List<GalleryImageInfo> ->
                viewModel.updateGalleryImages(images)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val MIN_SELECTED_ITEM = 1
    }
}