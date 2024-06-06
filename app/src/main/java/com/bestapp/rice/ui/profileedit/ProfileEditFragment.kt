package com.bestapp.rice.ui.profileedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentProfileEditBinding
import com.bestapp.rice.util.loadOrDefault
import com.bestapp.rice.util.setVisibility
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding: FragmentProfileEditBinding
        get() = _binding!!

    private val navArgs: ProfileEditFragmentArgs by navArgs()

    private val viewModel: ProfileEditViewModel by viewModels {
        ProfileEditViewModelFactory()
    }

    private var onLoadingJob: Job = Job()

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

        viewModel.setUserInfo(navArgs.profileEditArg)
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
            hideInput()
            clearFocus()
            it.clearFocus()
            // TODO : ProfileImageSelectFragment 구현 시 PhotoPicker가 아닌 이미지를 불러오는 로직 구현 예정
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.edtNickname.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideInput()
                clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        binding.edtNickname.doOnTextChanged { text, _, _, count ->
            val newNickname = text.toString()
            binding.btnSubmit.isEnabled =
                newNickname.length >= resources.getInteger(R.integer.min_nickname_length)
            viewModel.updateNickname(newNickname)
        }
        binding.btnSubmit.setOnClickListener {
            hideInput()
            clearFocus()
            viewModel.submit()
        }
        binding.ivRemoveProfileImage.setOnClickListener {
            hideInput()
            clearFocus()
            viewModel.onRemoveProfileImage()
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    private fun hideInput() {
        getSystemService(requireContext(), InputMethodManager::class.java)?.hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }

    private fun clearFocus() {
        binding.vForInterceptFocus.requestFocus()
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    setUI(state)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.submitUiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    when (state) {
                        SubmitUiState.Uploading -> {
                            onLoadingJob = launch {
                                setLoading(true)
                            }
                        }

                        SubmitUiState.SubmitNicknameFail -> {
                            onLoadingJob.cancel()
                            setLoading(false)

                            val message = getString(R.string.message_when_edit_nickname_fail)
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }

                        SubmitUiState.SubmitProfileFail -> {
                            onLoadingJob.cancel()
                            setLoading(false)

                            val message = getString(R.string.message_when_edit_profile_image_fail)
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }

                        SubmitUiState.Success -> {
                            onLoadingJob.cancel()
                            setLoading(false)

                            if (!findNavController().popBackStack()) {
                                requireActivity().finish()
                            }
                            return@collectLatest
                        }
                    }
                }
        }
    }

    private suspend fun setLoading(isInLoading: Boolean) {
        if (isInLoading) {
            delay(500)
        }
        binding.vModalBackground.setVisibility(isInLoading)
        binding.cpiLoading.setVisibility(isInLoading)
    }

    private fun setUI(state: ProfileEditUiState) {
        binding.ivProfile.loadOrDefault(state.profileImage)
        if (state.isNicknameAppliedToView) {
            binding.edtNickname.setText(state.nickname)
        }
    }

    override fun onDestroyView() {
        onLoadingJob.cancel()
        _binding = null

        super.onDestroyView()
    }
}