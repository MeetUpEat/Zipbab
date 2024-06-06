package com.bestapp.rice.ui.profileedit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentProfileEditBinding
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ProfileEditFragment : Fragment(){

    private var _binding: FragmentProfileEditBinding? = null
    private val binding: FragmentProfileEditBinding
        get() = _binding!!

    private val navArgs: ProfileEditFragmentArgs by navArgs()

    private val viewModel: ProfileEditViewModel by viewModels {
        ProfileEditViewModelFactory()
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.updateProfileThumbnail(uri)
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setUserInfo(navArgs.userUiState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDefaultUI()
        setListener()
        setObserve()
    }

    private fun setDefaultUI() {
        binding.ivProfile.clipToOutline = true
    }

    private fun setListener() {
        binding.ivProfile.setOnClickListener {
            // ProfileImageSelectFragment 구현 시 PhotoPicker가 아닌 이미지를 불러오는 로직 구현 예정
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.edtNickname.doOnTextChanged { text, _, _, count ->
            binding.btnSubmit.isEnabled =
                count >= resources.getInteger(R.integer.min_nickname_length)
            viewModel.updateNickname(text.toString())
        }
        binding.btnSubmit.setOnClickListener {
            viewModel.submit()
        }
        binding.ivRemoveProfileImage.setOnClickListener {
            viewModel.onRemoveProfileImage()
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.userUiState.flowWithLifecycle(lifecycle)
                    .collectLatest { userUiState ->
                        setUI(userUiState)
                    }
            }
            launch {
                viewModel.message.flowWithLifecycle(lifecycle)
                    .collectLatest { profileEditMessage ->
                        val message = when (profileEditMessage) {
                            ProfileEditMessage.EDIT_NICKNAME_FAIL -> getString(R.string.message_when_edit_nickname_fail)
                            ProfileEditMessage.EDIT_PROFILE_IMAGE_FAIL -> getString(R.string.message_when_edit_profile_image_fail)
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
            launch {
                viewModel.isProfileUpdateSuccessful.flowWithLifecycle(lifecycle)
                    .collectLatest { isSuccess ->
                        if (isSuccess) {
                            if (!findNavController().popBackStack()) {
                                requireActivity().finish()
                            }
                        }
                    }
            }
        }
    }

    private fun setUI(userUiState: UserUiState) {
        binding.ivProfile.load(userUiState.profileImage) {
            placeholder(R.drawable.sample_profile_image)
        }
        binding.edtNickname.setText(userUiState.nickName)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}