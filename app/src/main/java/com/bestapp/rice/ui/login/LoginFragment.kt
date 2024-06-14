package com.bestapp.rice.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel.loginLoad()
    }

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
        loginViewModel.loginLoad.observe(viewLifecycleOwner) {
            binding.cbRemember.isChecked = it.isNotEmpty()
            binding.etvEmail.setText(it)
        }

        loginViewModel.login.observe(viewLifecycleOwner) { result ->
            if(result.second) {
                loginViewModel.updateDocumentId(result.first)
            } else {
                Toast.makeText(context, "이메일이나 비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.isDone.collect {
                if(it) {
                    findNavController().popBackStack()
                }
            }
        }

        binding.bLogin.setOnClickListener {
            if(binding.cbRemember.isChecked) {
                loginViewModel.loginSave(binding.etvEmail.text.toString())
            } else {
                loginViewModel.loginSave("")
            }

            loginViewModel.loginCompare(binding.etvEmail.text.toString(), binding.etvPassword.editText!!.text.toString())
        }

        binding.cbRemember.setOnCheckedChangeListener { _, check ->
            if(check) {
                Toast.makeText(context, "아이디 기억 적용 및 복원", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "아이디 기억 해제 및 삭제", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.bBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun bindViews() {
        binding.etvEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etvPassword.editText!!.length() > 0 && binding.etvEmail.length() > 0) {
                    loginVisable()
                } else {
                    loginDisVisable()
                }
            }
        })

        binding.etvPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etvEmail.length() > 0 && binding.etvPasswordInput.length() > 0) {
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