package com.bestapp.rice.ui.setting

import android.os.Bundle
import android.view.View
import com.bestapp.rice.databinding.FragmentSettingBinding
import com.bestapp.rice.ui.BaseFragment

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
    }

    private fun setUI() {
        binding.ivProfileImage.clipToOutline = true
    }
}