package com.bestapp.zipbab.ui.profileedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentProfileEditBinding
import com.bestapp.zipbab.model.args.ImageUi
import com.bestapp.zipbab.ui.profileimageselect.ProfileImageSelectFragment
import com.bestapp.zipbab.util.loadOrDefault
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding: FragmentProfileEditBinding
        get() = _binding!!

    private val navArgs: ProfileEditFragmentArgs by navArgs()

    private val viewModel: ProfileEditViewModel by viewModels()

    private var onLoadingJob: Job = Job()

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

        viewModel.setUserInfo(navArgs.profileEditUi)
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
            val action =
                ProfileEditFragmentDirections.actionProfileEditFragmentToProfileImageSelectFragment()
            findNavController().navigate(action)
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
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<ImageUi>(ProfileImageSelectFragment.PROFILE_IMAGE_SELECT_KEY).observe(viewLifecycleOwner) {
                remove<ImageUi>(ProfileImageSelectFragment.PROFILE_IMAGE_SELECT_KEY)
                viewModel.updateProfileThumbnail(it.uri)
            }
        }
    }

    private suspend fun setLoading(isInLoading: Boolean) {
        if (isInLoading) {
            delay(500)
        }
        binding.vModalBackground.isVisible = isInLoading
        binding.cpiLoading.isVisible = isInLoading
    }

    private fun setUI(state: ProfileEditUiState) {
        binding.ivProfile.loadOrDefault(state.profileImage)
        binding.ivRemoveProfileImage.isGone = state.profileImage.isBlank()

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