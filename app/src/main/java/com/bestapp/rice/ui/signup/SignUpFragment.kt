package com.bestapp.rice.ui.signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.bestapp.rice.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.databinding.FragmentSignUpBinding
import java.util.Calendar
import java.util.regex.Pattern


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!

    private var textTypeChange = true
    private lateinit var signUpViewModel: SignUpViewModel

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

    @SuppressLint("SetTextI18n")
    private fun bindViews() {
        val posts : List<Post> = listOf()
        val placeLocation = PlaceLocation(
            locationAddress = "",
            locationLat = "",
            locationLong = ""
        )
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

        signUpViewModel.isSignUpState.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "잘못된 경로 입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bSignUp.setOnClickListener {

            signUpViewModel.userDataSave(user)
        }

        binding.bCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val listener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                val date = "$i.${i2 + 1}.$i3"
                binding.etvDate.setText(date)
            }

            val picker = DatePickerDialog(requireContext(), listener, year, month, day)
            picker.show()
        }

        binding.bPassword.setOnClickListener {
            if(textTypeChange) {
                binding.etvPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                textTypeChange = false
            } else {
                binding.etvPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                textTypeChange = true
            }
        }
    }

    private fun editTextViews() {
        val minNumber = 2
        val dateNumber = 8
        val exceptionNumber = 0
        binding.etvName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.etvName.length() > minNumber) {
                    binding.tvDate.isVisible = true
                    binding.etvDate.isVisible = true
                    binding.bCalendar.isVisible = true
                } else {
                    binding.tvDate.isVisible = false
                    binding.etvDate.isVisible = false
                    binding.bCalendar.isVisible = false
                }
            }
        })

        binding.etvDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.etvDate.length() > dateNumber) {
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
                if(binding.etvEmail.length() > minNumber) {
                    binding.etvPassword.isVisible = true
                    binding.tvPassword.isVisible = true
                    binding.bPassword.isVisible = true
                } else {
                    binding.etvPassword.isVisible = false
                    binding.tvPassword.isVisible = false
                    binding.bPassword.isVisible = false
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