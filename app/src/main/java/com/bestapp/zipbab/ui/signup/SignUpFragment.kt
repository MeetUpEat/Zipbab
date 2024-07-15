package com.bestapp.zipbab.ui.signup

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!

    private val signUpViewModel: SignUpViewModel by viewModels()

    private val mTransform = Linkify.TransformFilter { _, _ -> "" }
    private val patternUrl = Pattern.compile("이용약관")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDefaultUI()
        setListener()
        setObserve()
    }

    private fun setDefaultUI() {
        val spannableString = SpannableString(binding.tvTerms.text)

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.main_color, requireActivity().theme)),
            resources.getInteger(R.integer.start_position),
            resources.getInteger(R.integer.end_position),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTerms.text = spannableString

        binding.tilNickname.helperText = getString(R.string.signup_helper_text, resources.getInteger(R.integer.min_nickname_length), resources.getInteger(R.integer.max_nickname_length))
        binding.tilPassword.helperText = getString(R.string.signup_helper_text, resources.getInteger(R.integer.min_password_length), resources.getInteger(R.integer.max_password_length))
    }

    private fun setListener() = with(binding) {
        tvTerms.setOnClickListener {
            val newState = cbTerms.isChecked.not()
            cbTerms.isChecked = newState
            signUpViewModel.updateTermsAgree(newState)
        }

        cbTerms.setOnCheckedChangeListener { v, check ->
            if (v.isPressed || v.isFocused) {
                hideInput()
                signUpViewModel.onTermClick(check)
            }
        }

        vDummyForRemoveFocus.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideInput()
            }
        }

        mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }

        btnSignUp.setOnClickListener {
            signUpViewModel.onSignUp()
        }

        tilNickname.editText?.doOnTextChanged { text, _, _, _ ->
            signUpViewModel.updateNickname(text.toString())
        }
        tilEmail.editText?.doOnTextChanged { text, _, _, _ ->
            signUpViewModel.updateEmail(text.toString())
        }
        tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            signUpViewModel.updatePassword(text.toString())
        }
        tilPasswordCompare.editText?.doOnTextChanged { text, _, _, _ ->
            signUpViewModel.updatePasswordCompare(text.toString())
        }
    }

    private fun hideInput() {
        ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)?.hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    signUpViewModel.requestPrivacyUrl.collectLatest { privacy ->
                        if (privacy.link.isNotEmpty()) {
                            Linkify.addLinks(
                                binding.tvTerms,
                                patternUrl,
                                privacy.link,
                                null,
                                mTransform
                            )
                        }
                    }
                }

                launch {
                    signUpViewModel.inputValidation.collect { state ->
                        binding.tilNickname.error = if (state.nickname) null else binding.tilNickname.helperText
                        binding.tilEmail.error = if (state.email) null else getString(R.string.signup_email_format_not_correct)
                        binding.tilPassword.error = if (state.password) null else binding.tilPassword.helperText
                        binding.tilPasswordCompare.error = if (state.passwordCompare) null else getString(R.string.signup_password_compare_not_correct)
                        binding.btnSignUp.isEnabled = state.isValid()
                    }
                }

                launch {
                    signUpViewModel.signUpState.collect { state ->
                        when (state) {
                            SignUpState.Default -> Unit
                            SignUpState.DuplicateEmail -> {
                                signUpViewModel.resetSignUpState()
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.signup_duplicate_email),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            SignUpState.Fail -> {
                                signUpViewModel.resetSignUpState()
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.signup_fail),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is SignUpState.Success -> {
                                signUpViewModel.resetSignUpState()
                                findNavController().popBackStack(R.id.loginGraph, true)
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