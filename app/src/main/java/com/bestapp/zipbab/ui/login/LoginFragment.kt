package com.bestapp.zipbab.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentLoginBinding
import com.bestapp.zipbab.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

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

    private fun setListener() = with(binding) {
        btnLogin.setOnClickListener {
            loginViewModel.onLogin()
        }

        cbRemember.setOnCheckedChangeListener { button, check ->
            if (button.isPressed.not()) {
                return@setOnCheckedChangeListener
            }
            loginViewModel.updateIdRemember(check)
        }

        tvSignUp.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            action.safeNavigate(this@LoginFragment)
        }

        mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }

        tilEmail.editText?.doOnTextChanged { text, _, _, _ ->
            loginViewModel.updateId(text.toString())
        }

        tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            loginViewModel.updatePw(text.toString())
        }

        tilPassword.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                vDummyForRemoveFocus.requestFocus()
                hideInput()
                loginViewModel.onLogin()
                true
            } else {
                false
            }
        }

        tvRemember.setOnClickListener {
            val newState = cbRemember.isChecked.not()
            cbRemember.isChecked = newState
            loginViewModel.updateIdRemember(newState)
        }
    }

    private fun hideInput() {
        getSystemService(requireContext(), InputMethodManager::class.java)?.hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginViewModel.savedID.collect { id ->
                        if (id.isBlank()) {
                            return@collect
                        }
                        binding.cbRemember.isChecked = id.isNotBlank()
                        binding.tilEmail.editText?.setText(id)

                        loginViewModel.idRestored()
                    }
                }
                launch {
                    loginViewModel.inputState.collect {
                        binding.btnLogin.isEnabled = it
                    }
                }
                launch {
                    loginViewModel.isRememberId.collect {
                        binding.cbRemember.isChecked = it
                    }
                }
                launch {
                    loginViewModel.loginState.collect { state ->
                        when (state) {
                            LoginState.Default -> Unit
                            LoginState.Fail -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.login_fail),
                                    Toast.LENGTH_SHORT
                                ).show()
                                loginViewModel.onLoginStateUsed()
                            }

                            LoginState.Success -> {
                                loginViewModel.onLoginStateUsed()
                                if (!findNavController().popBackStack()) {
                                    requireActivity().finish()
                                }
                            }
                        }
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