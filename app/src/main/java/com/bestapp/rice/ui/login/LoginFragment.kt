package com.bestapp.rice.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentLoginBinding
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.MainActivity
import com.google.android.material.internal.TextWatcherAdapter


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private var typeChange = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonListener()
        bindViews()
    }

    private fun buttonListener() {
        binding.loginButton.setOnClickListener {
            //firestore에 저장한값 가져와서 로그인 정보 비교
            //AppSettingRepository를 통해 DataStore에 간접적으로 요청
            findNavController().popBackStack()
        }

        binding.checkButton.setOnCheckedChangeListener { compoundButton, check ->

            if(check) {
                //TODO datastore
            } else {
                //TODO datastore값 삭제
            }
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.visibleEditText.setOnClickListener {
            if(typeChange) {
                binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                typeChange = false
            } else {
                binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                typeChange = true
            }
        }
    }

    private fun bindViews() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (binding.passwordEditText.length() > 0 && binding.emailEditText.length() > 0) {
                        loginVisable()
                    } else {
                        loginDisVisable()
                    }
                }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.emailEditText.length() > 0 && binding.passwordEditText.length() > 0) {
                    loginVisable()
                } else {
                    loginDisVisable()
                }
            }
        })
    }

    private fun loginVisable() {
        binding.loginButton.isEnabled = true
        binding.loginButton.isClickable = true
        binding.loginButton.setBackgroundResource(R.drawable.background_button)
    }

    private fun loginDisVisable() {
        binding.loginButton.isEnabled = false
        binding.loginButton.isClickable = false
        binding.loginButton.setBackgroundResource(R.drawable.background_button_disable)
    }
}