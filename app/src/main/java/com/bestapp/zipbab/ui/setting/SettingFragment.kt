package com.bestapp.zipbab.ui.setting

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.BuildConfig
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentSettingBinding
import com.bestapp.zipbab.model.UserUiState
import com.bestapp.zipbab.util.loadOrDefault
import com.bestapp.zipbab.util.safeNavigate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding: FragmentSettingBinding
        get() = _binding!!

    private val viewModel: SettingViewModel by viewModels()
    private lateinit var clipboardManager: ClipboardManager

    private var userUiState: UserUiState = UserUiState()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init()
        clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDefaultUI()
        setObserve()
        setListener()
    }

    private fun setDefaultUI() = with(binding) {
        // minSdk로 인해 xml attribute가 아닌 코드에서 설정함
        ivProfileImage.clipToOutline = true

        viewProfile.tvTitle.text = getString(R.string.setting_profile_row_title)
        viewProfile.tvDescription.text = getString(R.string.setting_profile_row_description)
        viewProfile.ivIcon.setImageResource(R.drawable.baseline_person_24)

        viewMeeting.tvTitle.text = getString(R.string.setting_meeting_row_title)
        viewMeeting.tvDescription.text = getString(R.string.setting_meeting_row_description)
        viewMeeting.ivIcon.setImageResource(R.drawable.baseline_people_24)

        viewAlert.tvTitle.text = getString(R.string.setting_alert_row_title)
        viewAlert.tvDescription.text = getString(R.string.setting_alert_row_description)
        viewAlert.ivIcon.setImageResource(R.drawable.baseline_notifications_none_24)

        viewPrivacyPolicy.tvTitle.text =
            getString(R.string.setting_privacy_policy_row_title)
        viewPrivacyPolicy.tvDescription.text =
            getString(R.string.setting_privacy_policy_row_description)
        viewPrivacyPolicy.ivIcon.setImageResource(R.drawable.baseline_remove_red_eye_24)

        viewLocationPolicy.tvTitle.text = getString(R.string.setting_location_policy_row_title)
        viewLocationPolicy.tvDescription.text =
            getString(R.string.setting_location_policy_row_description)
        viewLocationPolicy.ivIcon.setImageResource(R.drawable.baseline_my_location_24)

        viewVersion.tvTitle.text = getString(R.string.setting_version_row_title)
        viewVersion.tvDescription.text =
            getString(R.string.version_format).format(BuildConfig.VERSION_NAME)
        viewVersion.ivIcon.setImageResource(R.drawable.baseline_code_24)
        viewVersion.ivEnter.visibility = View.GONE
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfoLodeState.collect { state ->
                        when (state) {
                            LoadingState.Default -> {
                                setListenerRequireInternet(isNotLoadingYet = false)
                            }
                            is LoadingState.Done -> {
                                setListenerRequireInternet(isNotLoadingYet = false)
                            }
                            LoadingState.OnLoading -> {
                                setListenerRequireInternet(isNotLoadingYet = true)
                            }
                        }
                    }
                }
                launch {
                    viewModel.userUiState.collect { userUiState ->
                        this@SettingFragment.userUiState = userUiState
                        setUI(userUiState)
                        copyTextThenShow(userUiState.userDocumentID)
                    }
                }
                launch {
                    viewModel.message.collect { message ->
                        val text = when (message) {
                            SettingMessage.LOGOUT_FAIL -> getString(R.string.message_when_log_out_fail)
                            SettingMessage.SIGN_OUT_FAIL -> getString(R.string.message_when_sign_out_fail)
                            SettingMessage.SIGN_OUT_IS_NOT_ALLOWED -> getString(R.string.sign_out_is_not_allowed)
                        }
                        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
                    }
                }
                launch {
                    viewModel.requestDeleteUrl.collect { url ->
                        binding.userDocumentIdInstructionView.tvUrl.setOnClickListener {
                            // 인터넷 연결이 느려서 로딩이 안 된 경우 대응
                            if (url.isBlank()) {
                                showNotYetLoaded(getString(R.string.text_for_delete_request_title))
                                return@setOnClickListener
                            }
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        }
                    }
                }
                launch {
                    viewModel.requestPrivacyUrl
                        .collect { privacy ->
                            binding.viewPrivacyPolicy.root.setOnClickListener {
                                // 인터넷 연결이 느려서 로딩이 안 된 경우 대응
                                if (privacy.link.isBlank()) {
                                    showNotYetLoaded(getString(R.string.setting_privacy_policy_row_title))
                                    return@setOnClickListener
                                }
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacy.link))
                                startActivity(intent)
                            }
                        }
                }
                launch {
                    viewModel.requestLocationPolicyUrl
                        .collect { privacy ->
                            binding.viewLocationPolicy.root.setOnClickListener {
                                // 인터넷 연결이 느려서 로딩이 안 된 경우 대응
                                if (privacy.link.isBlank()) {
                                    showNotYetLoaded(getString(R.string.setting_location_policy_row_title))
                                    return@setOnClickListener
                                }
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacy.link))
                                startActivity(intent)
                            }
                        }
                }
            }
        }
    }

    private fun setListener() = with(binding) {
        viewAlert.root.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT
            ).show()
//            val action = SettingFragmentDirections.actionSettingFragmentToAlertSettingFragment()
//            findNavController().navigate(action)
        }
        ivDistinguishNumInfo.setOnClickListener {
            userDocumentIdInstructionView.root.isVisible = true
        }
    }

    private fun setListenerRequireInternet(isNotLoadingYet: Boolean) = with(binding) {
        ivProfileImage.setOnClickListener {
            if (isNotLoadingYet) {
                showNotYetLoaded(getString(R.string.user_info))
                return@setOnClickListener
            }
            val action = if (userUiState.isLoggedIn) {
                SettingFragmentDirections.actionSettingFragmentToProfileFragment(userUiState.userDocumentID)
            } else {
                SettingFragmentDirections.actionSettingFragmentToLoginGraph()
            }
            action.safeNavigate(this@SettingFragment)
        }
        viewProfile.root.setOnClickListener {
            if (isNotLoadingYet) {
                showNotYetLoaded(getString(R.string.user_info))
                return@setOnClickListener
            }
            val action =
                SettingFragmentDirections.actionSettingFragmentToProfileFragment(userUiState.userDocumentID)
            action.safeNavigate(this@SettingFragment)
        }
        viewMeeting.root.setOnClickListener {
            if (isNotLoadingYet) {
                showNotYetLoaded(getString(R.string.user_info))
                return@setOnClickListener
            }
            val action =
                SettingFragmentDirections.actionSettingFragmentToMeetingListFragment()
            action.safeNavigate(this@SettingFragment)
        }
        btnLogin.setOnClickListener {
            if (isNotLoadingYet) {
                showNotYetLoaded(getString(R.string.user_info))
                return@setOnClickListener
            }
            val action = SettingFragmentDirections.actionSettingFragmentToLoginGraph()
            action.safeNavigate(this@SettingFragment)
        }
        btnLogout.setOnClickListener {
            if (isNotLoadingYet) {
                showNotYetLoaded(getString(R.string.user_info))
                return@setOnClickListener
            }
            viewModel.logout()
            Toast.makeText(requireContext(), getString(R.string.logout_done), Toast.LENGTH_SHORT)
                .show()
        }
        btnRegister.setOnClickListener {
            if (isNotLoadingYet) {
                showNotYetLoaded(getString(R.string.user_info))
                return@setOnClickListener
            }
            val uri = Uri.parse("android-app://com.bestapp.zipbab/signup")
            findNavController().navigate(uri)
        }
        btnUnregister.setOnClickListener {
            if (isNotLoadingYet) {
                showNotYetLoaded(getString(R.string.user_info))
                return@setOnClickListener
            }
            signOutDialog.show()
        }
    }

    private fun showNotYetLoaded(actionInfo: String) {
        val message = getString(R.string.not_yet_loaded, actionInfo)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun copyTextThenShow(text: String) {
        binding.tvDistinguishNum.setOnClickListener {
            val clip = ClipData.newPlainText(getString(R.string.label_user_document_id), text)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(
                requireContext(),
                getString(R.string.user_document_id_is_copied), Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun signOut() {
        viewModel.signOut()
    }

    private fun setUI(userUiState: UserUiState) {
        if (userUiState.isLoggedIn) {
            setMemberUI(userUiState)
            return
        }
        setNonMemberUI()
    }

    private fun setNonMemberUI() = with(binding) {
        tvNickname.text = getString(R.string.nonmember)
        tvDistinguishNum.visibility = View.GONE
        ivDistinguishNumInfo.visibility = View.GONE
        ivProfileImage.setImageResource(R.drawable.sample_profile_image)

        btnLogin.visibility = View.VISIBLE
        btnLogout.visibility = View.GONE

        btnRegister.visibility = View.VISIBLE
        btnUnregister.visibility = View.GONE

        setMeetingAndProfileEnabled(false)
    }

    private fun setMemberUI(userUiState: UserUiState) = with(binding) {
        tvNickname.text = userUiState.nickname
        tvDistinguishNum.visibility = View.VISIBLE
        ivDistinguishNumInfo.visibility = View.VISIBLE

        tvDistinguishNum.text =
            getString(R.string.profile_distinguish_format_8).format(userUiState.userDocumentID)
        tvDistinguishNum.paintFlags =
            tvDistinguishNum.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        ivProfileImage.loadOrDefault(userUiState.profileImage)

        btnLogin.visibility = View.GONE
        btnLogout.visibility = View.VISIBLE

        btnRegister.visibility = View.GONE
        btnUnregister.visibility = View.VISIBLE

        setMeetingAndProfileEnabled(true)
    }

    private fun setMeetingAndProfileEnabled(isEnabled: Boolean) = with(binding) {
        viewProfile.root.isEnabled = isEnabled
        viewProfile.tvTitle.isEnabled = isEnabled
        viewProfile.tvDescription.isEnabled = isEnabled
        viewProfile.ivIcon.isEnabled = isEnabled
        viewProfile.ivEnter.isEnabled = isEnabled

        viewMeeting.root.isEnabled = isEnabled
        viewMeeting.tvTitle.isEnabled = isEnabled
        viewMeeting.tvDescription.isEnabled = isEnabled
        viewMeeting.ivIcon.isEnabled = isEnabled
        viewMeeting.ivEnter.isEnabled = isEnabled
    }

    fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val instructionView = binding.userDocumentIdInstructionView.root
        if (instructionView.isVisible && isWithOutViewBounds(instructionView, event)) {
            instructionView.visibility = View.GONE
            return true
        }
        return false
    }

    private fun isWithOutViewBounds(view: View, event: MotionEvent): Boolean {
        val xy = IntArray(2)
        view.getLocationOnScreen(xy)
        val (x, y) = xy

        return event.x < x || event.x > x + view.width || event.y < y || event.y > y + view.height
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
