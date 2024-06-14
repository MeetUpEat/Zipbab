package com.bestapp.zipbab.ui.login

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
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentLoginBinding
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

        loginViewModel.loadSavedID()
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

        setListener()
        setObserve()
    }

    private fun setListener() {
        binding.bLogin.setOnClickListener {
               loginViewModel.tryLogin(
                   binding.cbRemember.isChecked,
                   binding.etvEmail.text.toString(),
                   binding.etvPassword.editText!!.text.toString()
               )
        }

        binding.cbRemember.setOnCheckedChangeListener { button, check ->
            if (button.isPressed.not()) {
                return@setOnCheckedChangeListener
            }
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

        binding.etvEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                changeLoginEnabled(checkLoginConditionSatisfied())
            }
        })

        binding.etvPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                changeLoginEnabled(checkLoginConditionSatisfied())
            }
        })
    }

    private fun checkLoginConditionSatisfied(): Boolean = binding.etvEmail.length() > 0 && binding.etvPasswordInput.length() > 0

    private fun changeLoginEnabled(isEnabled: Boolean) {
        binding.bLogin.isEnabled = isEnabled
        val backgroundResource = if (isEnabled) {
            R.drawable.background_button
        } else {
            R.drawable.background_button_disable
        }
        binding.bLogin.setBackgroundResource(backgroundResource)
    }

    private fun loginDisVisable() {
        binding.bLogin.isEnabled = false
        binding.bLogin.setBackgroundResource(R.drawable.background_button_disable)
    }

    private fun setObserve() {
        loginViewModel.savedID.observe(viewLifecycleOwner) {
            binding.cbRemember.isChecked = it.isNotEmpty()
            binding.etvEmail.setText(it)
        }

        loginViewModel.login.observe(viewLifecycleOwner) { userDocumentID ->
            if(userDocumentID.isNotEmpty()) {
                loginViewModel.saveLoggedInfo(userDocumentID)
            } else {
                Toast.makeText(context, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.isDone.collect {
                when(it) {
                    MoveNavigation.GOBACK -> { findNavController().popBackStack() }
                    MoveNavigation.GOMEETINGMANGERAGEMENT -> {
                        val action = LoginFragmentDirections.actionLoginFragmentToMeetingManagementFragment(loginViewModel.getMeetingDocumentId())
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}