package com.bestapp.rice.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bestapp.rice.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private var typeChange = true
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonListener()
        bindViews()
    }

    private fun buttonListener() {
        loginViewModel.login.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "이메일이나 비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bLogin.setOnClickListener {
            loginViewModel.loginCompare(binding.etvEmail.text.toString(), binding.etvPassword.text.toString())
            loginViewModel.loginSave(binding.etvEmail.text.toString())//AppSettingRepository를 통해 DataStore에 간접적으로 요청
        }

        //datastore 값의 존재 유무에 따라서 setText 처리

        binding.cbRemember.setOnCheckedChangeListener { _, check ->

            if(check) {
                //binding.etvEmail.setText() //datastore값 가져오는 로직 생성 필요
            } else {
                //TODO 예외처리
            }
        }

        binding.bSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.bVisible.setOnClickListener {
            if(typeChange) {
                binding.etvPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                typeChange = false
            } else {
                binding.etvPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                typeChange = true
            }
        }
    }

    private fun bindViews() {
        binding.etvEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (binding.etvPassword.length() > 0 && binding.etvEmail.length() > 0) {
                        loginVisable()
                    } else {
                        loginDisVisable()
                    }
                }
        })

        binding.etvPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etvEmail.length() > 0 && binding.etvPassword.length() > 0) {
                    loginVisable()
                } else {
                    loginDisVisable()
                }
            }
        })
    }

    private fun loginVisable() {
        binding.bLogin.isEnabled = true
        binding.bLogin.isClickable = true
        binding.bLogin.setBackgroundResource(R.drawable.background_button)
    }

    private fun loginDisVisable() {
        binding.bLogin.isEnabled = false
        binding.bLogin.isClickable = false
        binding.bLogin.setBackgroundResource(R.drawable.background_button_disable)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}