package com.bestapp.rice.ui.signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.databinding.FragmentSignUpBinding
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.MainActivity
import com.google.android.material.internal.TextWatcherAdapter
import java.util.Calendar
import java.util.regex.Pattern


class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {
    private var textTypeChange = false
    private var editTextTypeChange = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        bindViews()
        editTextViews()
    }

    private fun initViews() {

        val fullText = binding.rememberText.text

        val spannableString = SpannableString(fullText)

        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#009EE2")),
            7,
            11,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.rememberText.text = spannableString

        val mTransform = Linkify.TransformFilter { match, url -> "" }
        val pattern = Pattern.compile("이용약관")

        Linkify.addLinks(binding.rememberText, pattern, "", null, mTransform)
    }

    @SuppressLint("SetTextI18n")
    private fun bindViews() {

        binding.signUpButton.setOnClickListener {
            //firestore 에 값저장
            findNavController().popBackStack()
        }

        binding.calendarButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                binding.dateEditText.setText("${i}/${i2 + 1}/${i3}")
            }

            val picker = context?.let { it1 -> DatePickerDialog(it1, listener, year, month, day) }
            picker?.show()
        }

        binding.passwordVisable.setOnClickListener {
            if(textTypeChange) {
                binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                textTypeChange = false
            } else {
                binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                textTypeChange = true
            }
        }

        binding.passwordCompareVisable.setOnClickListener {
            if(editTextTypeChange) {
                binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                editTextTypeChange = false
            } else {
                binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                editTextTypeChange = true
            }
        }
    }

    private fun editTextViews() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.nameEditText.length() > 2) {
                    binding.dateText.isVisible = true
                    binding.dateEditText.isVisible = true
                    binding.calendarButton.isVisible = true
                } else {
                    binding.dateText.isVisible = false
                    binding.dateEditText.isVisible = false
                    binding.calendarButton.isVisible = false
                }
            }
        })

        binding.dateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.dateEditText.length() > 2) {
                    binding.emailEditText.isVisible = true
                    binding.emailText.isVisible = true
                } else {
                    binding.emailEditText.isVisible = false
                    binding.emailText.isVisible = false
                }
            }
        })

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.emailEditText.length() > 2) {
                    binding.passwordEditText.isVisible = true
                    binding.passwordText.isVisible = true
                    binding.passwordVisable.isVisible = true
                } else {
                    binding.passwordEditText.isVisible = false
                    binding.passwordText.isVisible = false
                    binding.passwordVisable.isVisible = false
                }
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.passwordEditText.length() > 2) {
                    binding.passwordCompareEditText.isVisible = true
                    binding.passwordCompareText.isVisible = true
                    binding.passwordCompareVisable.isVisible = true
                } else {
                    binding.passwordCompareEditText.isVisible = false
                    binding.passwordCompareText.isVisible = false
                    binding.passwordCompareVisable.isVisible = false
                }
            }
        })

        if(binding.passwordEditText.text.toString() ==
            binding.passwordCompareEditText.text.toString() &&
            binding.passwordEditText.length() > 0) {
            binding.rememberText.isVisible = true
            binding.checkButton.isVisible = true
        } else {
            binding.rememberText.isVisible = false
            binding.checkButton.isVisible = false
        }

        binding.checkButton.setOnCheckedChangeListener { _, check ->
            binding.signUpButton.isVisible = check
        }
    }
}