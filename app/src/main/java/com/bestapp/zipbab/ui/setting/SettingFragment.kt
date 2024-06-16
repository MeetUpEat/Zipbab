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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.BuildConfig
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentSettingBinding
import com.bestapp.zipbab.model.UserUiState
import com.bestapp.zipbab.util.loadOrDefault
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

        binding.viewLocationPolicy.tvTitle.text = getString(R.string.setting_location_policy_row_title)
        binding.viewLocationPolicy.tvDescription.text = getString(R.string.setting_location_policy_row_description)
        binding.viewLocationPolicy.ivIcon.setImageResource(R.drawable.baseline_my_location_24)

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
                        copyTextThenShow(userUiState.userDocumentID)
                    }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.requestDeleteUrl.flowWithLifecycle(lifecycle)
                .collect { url ->
                    binding.userDocumentIdInstructionView.tvUrl.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.requestPrivacyUrl.flowWithLifecycle(lifecycle)
                .collect { privacy ->
                    binding.viewPrivacyPolicy.root.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacy.link))
                        startActivity(intent)
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.requestLocationPolicyUrl.flowWithLifecycle(lifecycle)
                .collect { privacy ->
                    binding.viewLocationPolicy.root.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacy.link))
                        startActivity(intent)
                    }
                }
        }
    }

    private fun setListener() {
        binding.ivProfileImage.setOnClickListener {
            val action = if (userUiState.isLoggedIn) {
                SettingFragmentDirections.actionSettingFragmentToProfileFragment(userUiState.userDocumentID)
            } else {
                SettingFragmentDirections.actionSettingFragmentToLoginFragment("")
            }
            findNavController().navigate(action)
        }
        binding.viewProfile.root.setOnClickListener {
            val action =
                SettingFragmentDirections.actionSettingFragmentToProfileFragment(userUiState.userDocumentID)
            findNavController().navigate(action)
        }
        binding.viewMeeting.root.setOnClickListener {
            val action =
                SettingFragmentDirections.actionSettingFragmentToMeetingListFragment()
            findNavController().navigate(action)
        }
        binding.viewAlert.root.setOnClickListener {
            Toast.makeText(requireContext(),
                getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT).show()
//            val action = SettingFragmentDirections.actionSettingFragmentToAlertSettingFragment()
//            findNavController().navigate(action)
        }
        binding.btnLogin.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToLoginFragment("")
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
        binding.ivDistinguishNumInfo.setOnClickListener {
            binding.userDocumentIdInstructionView.root.isVisible = true
        }

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

    private fun setNonMemberUI() {
        binding.tvNickname.text = getString(R.string.nonmember)
        binding.tvDistinguishNum.visibility = View.GONE
        binding.ivDistinguishNumInfo.visibility = View.GONE
        binding.ivProfileImage.setImageResource(R.drawable.sample_profile_image)

        binding.btnLogin.visibility = View.VISIBLE
        binding.btnLogout.visibility = View.GONE

        binding.btnRegister.visibility = View.VISIBLE
        binding.btnUnregister.visibility = View.GONE

        setMeetingAndProfileEnabled(false)
    }

    private fun setMemberUI(userUiState: UserUiState) {
        binding.tvNickname.text = userUiState.nickname
        binding.tvDistinguishNum.visibility = View.VISIBLE
        binding.ivDistinguishNumInfo.visibility = View.VISIBLE

        binding.tvDistinguishNum.text =
            getString(R.string.profile_distinguish_format_8).format(userUiState.userDocumentID)
        binding.tvDistinguishNum.paintFlags =
            binding.tvDistinguishNum.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.ivProfileImage.loadOrDefault(userUiState.profileImage)

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
