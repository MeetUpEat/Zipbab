package com.bestapp.rice.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
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
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
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
        val startPosition = resources.getInteger(R.integer.start_position)
        val endPosition = resources.getInteger(R.integer.end_position)


        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.main_color, requireActivity().theme)),
            startPosition,
            endPosition,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTerms.text = spannableString

        val mTransform = Linkify.TransformFilter { _, url -> "/view/dinglemingle" }
        val pattern = Pattern.compile("이용약관")

        Linkify.addLinks(binding.tvTerms, pattern, "https://sites.google.com", null, mTransform)
    }

    private fun bindViews() {
        val notificationList: List<com.bestapp.rice.data.model.remote.NotificationType> = listOf()
        val posts : List<Post> = listOf()
        val meetingReviews : List<String> = listOf()
        val placeLocation = PlaceLocation(
            locationAddress = "",
            locationLat = "",
            locationLong = ""
        )

        signUpViewModel.isSignUpState.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                Toast.makeText(context, "잘못된 경로 입니다.", Toast.LENGTH_SHORT).show()
            } else {
                signUpViewModel.saveDocumentId(it)
                findNavController().popBackStack()
            }
        }

        binding.bSignUp.setOnClickListener {
            val randomUUID = Random()
            val user = User(
                userDocumentID = "",
                uuid = "$randomUUID",
                nickname = binding.etvName.text.toString(),
                id = binding.etvEmail.text.toString(),
                pw = binding.etvPassword.text.toString(),
                profileImage = "",
                temperature = 0.0,
                meetingCount = 0,
                notificationList = notificationList,
                meetingReviews = meetingReviews,
                posts = posts,
                placeLocation = placeLocation
            )
            signUpViewModel.userDataSave(user)
        }
    }

    private fun editTextViews() {
        binding.etvName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.etvName.length() > resources.getInteger(R.integer.min_name)) {
                    binding.etvEmail.isVisible = true
                    binding.emailText.isVisible = true
                } else {
                    binding.etvEmail.isVisible = false
                    binding.emailText.isVisible = false
                }
            }
        })


        binding.etvEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.etvEmail.length() > resources.getInteger(R.integer.min_mail)) {
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
                if(binding.etvPassword.length() > resources.getInteger(R.integer.min_password)) {
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

        binding.etPasswordCompare.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val passwordText = binding.etvPassword.text.toString()
                val passwordEditText = binding.etPasswordCompare.text.toString()
                val passwordEditTextLength = binding.etvPassword.length()

                if(passwordText == passwordEditText && passwordEditTextLength > resources.getInteger(R.integer.exception_number)) {
                    binding.tvTerms.isVisible = true
                    binding.bCheck.isVisible = true
                    binding.etvPasswordCompare.helperText = "Password Match"
                    binding.etvPasswordCompare.setHelperTextColor(ContextCompat.getColorStateList(requireContext(), R.color.temperature_min_40))
                } else {
                    binding.tvTerms.isVisible = false
                    binding.bCheck.isVisible = false
                    binding.etvPasswordCompare.helperText = "Password Mismatch"
                    binding.etvPasswordCompare.setHelperTextColor(ContextCompat.getColorStateList(requireContext(), R.color.temperature_min_80))
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