package com.bestapp.zipbab.ui.signup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!

    // 각 입력 View의 visibility를 한 번에 수정하기 위한 변수
    private val inputViews by lazy {
        listOf(
            binding.emailText, binding.etvEmail, binding.etvEmailText,
            binding.tvPassword, binding.etvPassword, binding.etvPasswordInputEdit,
            binding.tvPasswordCompare, binding.etvPasswordCompare, binding.etPasswordCompare,
            binding.bCheck, binding.tvTerms,
            binding.bSignUp,
        )
    }

    private val signUpViewModel: SignUpViewModel by viewModels()

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

        initViews()
        setListener()
        setObserve()
    }

    private fun initViews() {
        signUpViewModel.getPrivacyUrl()

        val spannableString = SpannableString(binding.tvTerms.text)

        binding.tvTerms.setOnClickListener {
            binding.bCheck.isChecked = !binding.bCheck.isChecked
        }

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.main_color, requireActivity().theme)),
            resources.getInteger(R.integer.start_position),
            resources.getInteger(R.integer.end_position),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTerms.text = spannableString

        val mTransform = Linkify.TransformFilter { _, url -> "" }
        val patternUrl = Pattern.compile("이용약관")

        viewLifecycleOwner.lifecycleScope.launch {
            signUpViewModel.requestPrivacyUrl.collectLatest { privacy ->
                if (privacy.link.isNotEmpty()) {
                    Linkify.addLinks(binding.tvTerms, patternUrl, privacy.link, null, mTransform)
                }
            }
        }
    }

    private fun setObserve() {
        signUpViewModel.isSignUpState.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                Toast.makeText(context, "잘못된 경로 입니다.", Toast.LENGTH_SHORT).show()
            } else {
                signUpViewModel.saveDocumentId(it)
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
        }
        signUpViewModel.isAllValid.observe(viewLifecycleOwner) { state ->
            val startIndex = state.num
            for (index in 0 until startIndex) {
                inputViews[index].isVisible = true
            }
            for (index in startIndex until inputViews.size) {
                inputViews[index].isVisible = false
            }
        }
        signUpViewModel.emailValid.observe(viewLifecycleOwner) { isValid ->
            val color = if (isValid) {
                R.color.temperature_min_40
            } else {
                R.color.temperature_min_80
            }
            binding.etvEmailText.setTextColor(ContextCompat.getColor(requireContext(), color))
        }
        signUpViewModel.passwordValid.observe(viewLifecycleOwner) { isValid ->
            val (helperText, textColor) = if (isValid) {
                "Password Match" to ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.temperature_min_40
                )
            } else {
                "Password Mismatch" to ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.temperature_min_80
                )
            }
            binding.etvPasswordCompare.helperText = helperText
            binding.etvPasswordCompare.setHelperTextColor(textColor)
        }
    }

    private fun setListener() {
        setInputChangedListener()

        binding.bCheck.setOnCheckedChangeListener { _, check ->
            signUpViewModel.onTermClick(check)
        }

        binding.bBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.bSignUp.setOnClickListener {
            signUpViewModel.userDataSave()
        }
    }

    private fun setInputChangedListener() {
        binding.etvNameInputtext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                signUpViewModel.updateNickname(s.toString())
            }
        })

        binding.etvEmailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                signUpViewModel.updateEmail(s.toString())
            }
        })

        binding.etvPasswordInputEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                signUpViewModel.updatePassword(s.toString())
            }
        })

        binding.etPasswordCompare.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                signUpViewModel.updatePasswordCheck(s.toString())
            }
        })
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}