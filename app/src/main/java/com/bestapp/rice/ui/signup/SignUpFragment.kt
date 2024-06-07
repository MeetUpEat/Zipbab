package com.bestapp.rice.ui.signup

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentSignUpBinding
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.User
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!

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
        bindViews()
        editTextViews()
    }

    private fun initViews() {

        val fullText = binding.tvTerms.text

        val spannableString = SpannableString(fullText)
        val startPosition = 7
        val endPosition = 11

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.main_color, requireActivity().theme)),
            startPosition,
            endPosition,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTerms.text = spannableString

        val mTransform = Linkify.TransformFilter { _, _ -> "" }
        val pattern = Pattern.compile("이용약관")

        Linkify.addLinks(binding.tvTerms, pattern, "", null, mTransform)
    }

    private fun bindViews() {
        val posts : List<Post> = listOf()
        val placeLocation = PlaceLocation(
            locationAddress = "",
            locationLat = "",
            locationLong = ""
        )

        signUpViewModel.isSignUpState.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "잘못된 경로 입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bSignUp.setOnClickListener {
            val user = User(
                userDocumentID = "",
                nickname = binding.etvName.text.toString(),
                id = binding.etvEmail.text.toString(),
                pw = binding.etvPassword.text.toString(),
                profileImage = "",
                temperature = 0.0,
                meetingCount = 0,
                posts = posts,
                placeLocation = placeLocation
            )
            signUpViewModel.userDataSave(user)
        }
    }

    private fun editTextViews() {
        val minNumber = 2
        val exceptionNumber = 0
        binding.etvName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.etvName.length() > minNumber) {
                    binding.emailText.isVisible = true
                    binding.etvEmail.isVisible = true
                } else {
                    binding.emailText.isVisible = false
                    binding.etvEmail.isVisible = false
                }
            }
        })


        binding.etvEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.etvEmail.length() > minNumber) {
                    binding.etvPassword.isVisible = true
                    binding.tvPassword.isVisible = true
                } else {
                    binding.etvPassword.isVisible = false
                    binding.tvPassword.isVisible = false
                }
            }
        })

        binding.etvPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.etvPassword.length() > minNumber) {
                    binding.etvPasswordCompare.isVisible = true
                    binding.tvPasswordCompare.isVisible = true
                    binding.etPasswordCompare.isVisible = true
                } else {
                    binding.etvPasswordCompare.isVisible = false
                    binding.tvPasswordCompare.isVisible = false
                    binding.etPasswordCompare.isVisible = false
                }
            }
        })

        binding.etPasswordCompare.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val passwordText = binding.etvPassword.text.toString()
                val passwordEditText = binding.etPasswordCompare.text.toString()
                val passwordEditTextLength = binding.etvPassword.length()

                if(passwordText == passwordEditText && passwordEditTextLength > exceptionNumber) {
                    binding.tvTerms.isVisible = true
                    binding.bCheck.isVisible = true
                    binding.tvCompareResult.isVisible = true
                    binding.tvCompareResult.text = "비밀 번호가 일치 합니다."
                } else {
                    binding.tvTerms.isVisible = false
                    binding.bCheck.isVisible = false
                    binding.tvCompareResult.isVisible = false
                    binding.tvCompareResult.text = "비밀 번호가 일치 하지 않 습니다."
                }
            }
        })

        binding.bCheck.setOnCheckedChangeListener { _, check ->
            binding.bSignUp.isVisible = check
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}