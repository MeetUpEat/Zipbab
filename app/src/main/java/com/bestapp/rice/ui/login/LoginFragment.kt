package com.bestapp.rice.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private var typeChange = false

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

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    private fun buttonListener() {
        binding.bLogin.setOnClickListener {
            //firestore에 저장한값 가져와서 로그인 정보 비교
            //AppSettingRepository를 통해 DataStore에 간접적으로 요청
            findNavController().popBackStack()
        }

        //datastore 값의 존재 유무에 따라서 setText 처리

        binding.cbRemember.setOnCheckedChangeListener { _, check ->

            if (check) {
                //TODO datastore key: documentId, value: email
            } else {
                //TODO 예외처리
            }
        }

        binding.bSignUp.setOnClickListener {
            // TODO: id, pw입력 후 로그인 버튼 누르고 회원가입 버튼 누르면 runTimeError 발생하는 이슈 있음
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.etvVisible.setOnClickListener {
            if (typeChange) {
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
}