package com.bestapp.zipbab.ui.profilepostimageselect

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentProfilePostImageSelectBinding
import com.bestapp.zipbab.model.toGalleryUiState
import com.bestapp.zipbab.permission.ImagePermissionType
import com.bestapp.zipbab.permission.PermissionManager
import com.bestapp.zipbab.ui.profile.ProfileFragmentArgs
import com.bestapp.zipbab.ui.profileimageselect.ProfileImageSelectFragment
import com.bestapp.zipbab.ui.profilepostimageselect.model.SubmitUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePostImageSelectFragment : Fragment() {

    private var _binding: FragmentProfilePostImageSelectBinding? = null
    private val binding: FragmentProfilePostImageSelectBinding
        get() = _binding!!

    private val permissionManager = PermissionManager(this)

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

    private val selectedImageAdapter = SelectedImageAdapter {
        viewModel.update(it.toGalleryUiState())
    }
    private val galleryAdapter = PostGalleryAdapter { clickedItem ->
        viewModel.update(clickedItem)
    }

    private val args: ProfileFragmentArgs by navArgs()

    private val viewModel: PostImageSelectViewModel by viewModels()

    private var onLoadingCoroutineScope = CoroutineScope(Dispatchers.Main)

    private fun onGranted() {
        // 권한이 바뀐 경우, 페이징을 갱신해서 이미지를 새롭게 불러온다.
        galleryAdapter.refresh()
    }

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
                ImagePermissionType.FULL -> permissionManager.requestFullImageAccessPermission(
                    requestMultiplePermissionLauncher
                ) {
                    onGranted()
                }

                ImagePermissionType.PARTIAL -> permissionManager.requestPartialImageAccessPermission(
                    requestMultiplePermissionLauncher, true
                ) {
                    onGranted()
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
        setListener()
        setObserve()
    }

    private fun setRecyclerView() {
        binding.rvGallery.adapter = galleryAdapter
        binding.rvSelectedImage.adapter = selectedImageAdapter
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
            viewModel.selectedImageStatesFlow.flowWithLifecycle(lifecycle)
                .collectLatest { states ->
                    selectedImageAdapter.submitList(states.values.toList())
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.submitUiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    when (state) {
                        SubmitUiState.Fail -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.message_when_uploading_post_fail),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        SubmitUiState.Success -> {
                            onLoadingCoroutineScope.coroutineContext.cancelChildren()
                            binding.vModalBackground.isVisible = false
                            binding.cpiLoading.isVisible = false
                            if (!findNavController().popBackStack()) {
                                requireActivity().finish()
                            }
                        }

                        SubmitUiState.Uploading -> {
                            onLoadingCoroutineScope.launch {
                                delay(500)
                                binding.vModalBackground.isVisible = true
                                binding.cpiLoading.isVisible = true
                            }
                        }

                        SubmitUiState.Default -> Unit
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imageStatePagingDataFlow.flowWithLifecycle(lifecycle)
                .collectLatest {
                    galleryAdapter.submitData(it)
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
        if (permissionManager.isFullImageAccessGranted()) {
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
        viewModel.resetSubmitState()

        super.onDestroyView()
    }

    companion object {
        private const val MIN_SELECTED_ITEM = 1
    }
}