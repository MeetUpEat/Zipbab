package com.bestapp.rice.ui.setting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.bestapp.rice.BuildConfig
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentSettingBinding
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.ui.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val viewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(requireContext())
    }

    private var userUiState = UserUiState.Empty

    private val signOutDialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sign_out_dialog_title))
            .setMessage(getString(R.string.sign_out_dialog_message))
            .setNeutralButton(getString(R.string.sign_out_dialog_neutral)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.sign_out_dialog_positive)) { _, _ ->
                signOut()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDefaultUI()
        setObserve()
        setListener()
    }

    private fun setDefaultUI() {
        // minSdk로 인해 xml attribute가 아닌 코드에서 설정함
        binding.ivProfileImage.clipToOutline = true

        binding.viewProfile.tvTitle.text = getString(R.string.setting_profile_row_title)
        binding.viewProfile.tvDescription.text = getString(R.string.setting_profile_row_description)
        binding.viewProfile.ivIcon.setImageResource(R.drawable.baseline_person_24)

        binding.viewMeeting.tvTitle.text = getString(R.string.setting_meeting_row_title)
        binding.viewMeeting.tvDescription.text = getString(R.string.setting_meeting_row_description)
        binding.viewMeeting.ivIcon.setImageResource(R.drawable.baseline_people_24)

        binding.viewAlert.tvTitle.text = getString(R.string.setting_alert_row_title)
        binding.viewAlert.tvDescription.text = getString(R.string.setting_alert_row_description)
        binding.viewAlert.ivIcon.setImageResource(R.drawable.baseline_notifications_none_24)

        binding.viewPrivacyPolicy.tvTitle.text =
            getString(R.string.setting_privacy_policy_row_title)
        binding.viewPrivacyPolicy.tvDescription.text =
            getString(R.string.setting_privacy_policy_row_description)
        binding.viewPrivacyPolicy.ivIcon.setImageResource(R.drawable.baseline_remove_red_eye_24)

        binding.viewVersion.tvTitle.text = getString(R.string.setting_version_row_title)
        binding.viewVersion.tvDescription.text =
            getString(R.string.version_format).format(BuildConfig.VERSION_NAME)
        binding.viewVersion.ivIcon.setImageResource(R.drawable.baseline_code_24)
        binding.viewVersion.ivEnter.visibility = View.GONE
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.userUiState.flowWithLifecycle(lifecycle)
                    .collect { userUiState ->
                        this@SettingFragment.userUiState = userUiState
                        setUI(userUiState)
                    }
            }

            launch {
                viewModel.message.flowWithLifecycle(lifecycle)
                    .collect { message ->
                        val text = when (message) {
                            SettingMessage.LOGOUT_FAIL -> getString(R.string.message_when_log_out_fail)
                            SettingMessage.SIGN_OUT_FAIL -> getString(R.string.message_when_sign_out_fail)
                        }
                        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun setListener() {
        binding.ivProfileImage.setOnClickListener {
            val action = if (userUiState.userDocumentID == UserUiState.Empty.userDocumentID) {
                SettingFragmentDirections.actionSettingFragmentToLoginFragment()
            } else {
                SettingFragmentDirections.actionSettingFragmentToProfileFragment(userUiState.userDocumentID)
            }
            findNavController().navigate(action)
        }
        binding.viewMeeting.root.setOnClickListener {
            val action =
                SettingFragmentDirections.actionSettingFragmentToMeetingListFragment(userUiState)
            findNavController().navigate(action)
        }
        binding.viewProfile.root.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToProfileFragment(userUiState.userDocumentID)
            findNavController().navigate(action)
        }
        binding.viewAlert.root.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToAlertSettingFragment()
            findNavController().navigate(action)
        }
        binding.viewPrivacyPolicy.root.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToPrivacyPolicyFragment()
            findNavController().navigate(action)
        }
        binding.btnLogin.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
        binding.btnRegister.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
        binding.btnUnregister.setOnClickListener {
            signOutDialog.show()
        }
    }

    private fun signOut() {
        viewModel.signOut()
    }

    private fun setUI(userUiState: UserUiState) {
        if (userUiState == UserUiState.Empty) {
            setNonMemberUI()
            return
        }
        setMemberUI(userUiState)
    }

    private fun setNonMemberUI() {
        binding.tvNickname.text = getString(R.string.nonmember)
        binding.tvDistinguishNum.visibility = View.GONE
        binding.ivProfileImage.setImageResource(R.drawable.sample_profile_image)

        binding.btnLogin.visibility = View.VISIBLE
        binding.btnLogout.visibility = View.GONE

        binding.btnRegister.visibility = View.VISIBLE
        binding.btnUnregister.visibility = View.GONE

        setMeetingAndProfileEnabled(false)
    }

    private fun setMemberUI(userUiState: UserUiState) {
        binding.tvNickname.text = userUiState.nickName
        binding.tvDistinguishNum.text =
            getString(R.string.profile_distinguish_format_8).format(userUiState.userDocumentID)
        binding.ivProfileImage.load(userUiState.profileImage) {
            placeholder(R.drawable.sample_profile_image)
        }

        binding.btnLogin.visibility = View.GONE
        binding.btnLogout.visibility = View.VISIBLE

        binding.btnRegister.visibility = View.GONE
        binding.btnUnregister.visibility = View.VISIBLE

        setMeetingAndProfileEnabled(true)
    }

    private fun setMeetingAndProfileEnabled(isEnabled: Boolean) {
        binding.viewProfile.root.isEnabled = isEnabled
        binding.viewProfile.tvTitle.isEnabled = isEnabled
        binding.viewProfile.tvDescription.isEnabled = isEnabled
        binding.viewProfile.ivIcon.isEnabled = isEnabled
        binding.viewProfile.ivEnter.isEnabled = isEnabled


        binding.viewMeeting.root.isEnabled = isEnabled
        binding.viewMeeting.tvTitle.isEnabled = isEnabled
        binding.viewMeeting.tvDescription.isEnabled = isEnabled
        binding.viewMeeting.ivIcon.isEnabled = isEnabled
        binding.viewMeeting.ivEnter.isEnabled = isEnabled
    }
}