package com.bestapp.rice.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bestapp.rice.BuildConfig
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentSettingBinding
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.ui.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val viewModel: SettingViewModel by viewModels {
        SettingViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDefaultUI()
        setObserve()
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
        binding.viewVersion.tvDescription.text = getString(R.string.version_format).format(BuildConfig.VERSION_NAME)
        binding.viewVersion.ivIcon.setImageResource(R.drawable.baseline_code_24)
        binding.viewVersion.ivEnter.visibility = View.GONE
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.userUiState.flowWithLifecycle(lifecycle)
                    .collectLatest {
                        setUI(it)
                    }
            }
        }
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
    }
}