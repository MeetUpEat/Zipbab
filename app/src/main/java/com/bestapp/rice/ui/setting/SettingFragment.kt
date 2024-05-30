package com.bestapp.rice.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
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

        setViewAttribute()
        setObserve()
    }

    private fun setViewAttribute() {
        // minSdk로 인해 xml attribute가 아닌 코드에서 설정함
        binding.ivProfileImage.clipToOutline = true
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