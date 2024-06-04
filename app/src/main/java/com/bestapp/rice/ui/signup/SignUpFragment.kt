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
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
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

        val fullText = binding.termsText.text

        val spannableString = SpannableString(fullText)
        val startPosition = 7
        val endPosition = 11

        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor(R.color.main_color.toString())),
            startPosition,
            endPosition,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.termsText.text = spannableString

        val mTransform = Linkify.TransformFilter { _, _ -> "" }
        val pattern = Pattern.compile("이용약관")

        Linkify.addLinks(binding.termsText, pattern, "", null, mTransform)
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

            val listener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                val date = "$i${i2 + 1}$i3"
                binding.dateEditText.setText(date)
            }

            val picker = DatePickerDialog(requireContext(), listener, year, month, day)
            picker.show()
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
                binding.passwordCompareEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                editTextTypeChange = false
            } else {
                binding.passwordCompareEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                editTextTypeChange = true
            }
        }
    }

    private fun editTextViews() {
        val minNumber = 2
        val exceptionNumber = 0
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.nameEditText.length() > minNumber) {
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
                if(binding.dateEditText.length() > minNumber) {
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
                if(binding.emailEditText.length() > minNumber) {
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
                if(binding.passwordEditText.length() > minNumber) {
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

        binding.passwordCompareEditText.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val passwordText = binding.passwordEditText.text.toString()
                val passwordEditText = binding.passwordCompareEditText.text.toString()
                val passwordEditTextLength = binding.passwordEditText.length()
                if(passwordText == passwordEditText && passwordEditTextLength > exceptionNumber) {
                    binding.termsText.isVisible = true
                    binding.checkButton.isVisible = true
                    binding.compareResultText.isVisible = true
                    binding.compareResultText.text = "비밀 번호가 일치 합니다."
                } else {
                    binding.termsText.isVisible = false
                    binding.checkButton.isVisible = false
                    binding.compareResultText.isVisible = false
                    binding.compareResultText.text = "비밀 번호가 일치 하지 않 습니다."
                }
            }
        })

        binding.checkButton.setOnCheckedChangeListener { _, check ->
            binding.signUpButton.isVisible = check
        }
    }
}