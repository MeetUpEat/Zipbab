package com.bestapp.rice.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentLoginBinding
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.MainActivity
import com.google.android.material.internal.TextWatcherAdapter


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is MainActivity) mainActivity = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        bindViews()
    }

    private fun initViews() {
        binding.loginButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.checkButton.setOnCheckedChangeListener { compoundButton, check ->
            if(check) {
                //datastore값 저장
            } else {
                //datastore에 값 저장 안함
            }
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun bindViews() {
        binding.emailEditText.addTextChangedListener( @SuppressLint("RestrictedApi")
        object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.passwordEditText.length() > 0 && binding.emailEditText.length() > 0) {
                    binding.loginButton.isEnabled = true
                    binding.loginButton.isClickable = true
                    binding.loginButton.setBackgroundResource(R.drawable.background_button)
                } else {
                    binding.loginButton.isEnabled = false
                    binding.loginButton.isClickable = false
                }
            }
        })

        binding.passwordEditText.addTextChangedListener( @SuppressLint("RestrictedApi")
        object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.emailEditText.length() > 0 && binding.passwordEditText.length() > 0) {
                    binding.loginButton.isEnabled = true
                    binding.loginButton.isClickable = true
                    binding.loginButton.setBackgroundResource(R.drawable.background_button)
                } else {
                    binding.loginButton.isEnabled = false
                    binding.loginButton.isClickable = false
                }
            }
        })
    }
}