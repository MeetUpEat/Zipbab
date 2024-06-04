package com.bestapp.rice.ui.recruitment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.webkit.WebViewClient
import android.widget.TimePicker
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentRecruitmentBinding
import com.bestapp.rice.ui.BaseFragment
import java.util.Calendar

class RecruitmentFragment : BaseFragment<FragmentRecruitmentBinding>(FragmentRecruitmentBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        selectLister()
    }

    private fun initViews() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.completeButton.setOnClickListener {
            //firestore에 값저장
            findNavController().popBackStack()
        }

        binding.locationView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true//앱에서 자바스크립트 다룰수 있게 셋팅 추후 삭제예정
            loadUrl("https://map.kakao.com/link/map/37.402056,127.108212")//임시 webview 지정값

        }

        binding.timeButton.setOnClickListener {
            val timeCalendar = Calendar.getInstance()
            val hourCal = timeCalendar.get(Calendar.HOUR_OF_DAY)
            val minCal = timeCalendar.get(Calendar.MINUTE)
            val timePicker = TimePickerDialog.OnTimeSetListener{ _, h, m ->
                val time = "$h:$m"
                binding.timeEdit.setText(time)
            }

            val picker = TimePickerDialog(requireContext(), timePicker, hourCal, minCal, true)
            picker.show()
        }

        binding.calendarButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val listener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                val date = "$i.${i2 + 1}.$i3"
                binding.dateEdit.setText(date)
            }

            val picker = DatePickerDialog(requireContext(), listener, year, month, day)
            picker.show()
        }
    }

    private fun selectLister() {
        binding.chipGroup.setOnCheckedStateChangeListener { chipGroup, _ ->
            val chipType = chipGroup.checkedChipId
            when(chipType) {
                R.id.first_type -> {chipGroupType(ChipType.FIRST)}
                R.id.second_type -> {chipGroupType(ChipType.SECOND)}
                R.id.third_type -> {chipGroupType(ChipType.THIRD)}
                R.id.fourth_type -> {chipGroupType(ChipType.FOURTH)}
                R.id.fifth_type -> {chipGroupType(ChipType.FIFTH)}
                R.id.sixth_type -> {chipGroupType(ChipType.SIXTH)}
                R.id.seventh_type -> {chipGroupType(ChipType.SEVENTH)}
                R.id.eighth_type -> {chipGroupType(ChipType.EIGHTH)}
                R.id.nineth_type -> {chipGroupType(ChipType.NINETH)}
            }
        }
    }

    private fun chipGroupType(type: ChipType) {
        when(type) {
            ChipType.FIRST -> {

            }
            ChipType.SECOND -> {

            }
            ChipType.THIRD -> {

            }
            ChipType.FOURTH -> {

            }
            ChipType.FIFTH -> {

            }
            ChipType.SIXTH -> {

            }
            ChipType.SEVENTH -> {

            }
            ChipType.EIGHTH -> {

            }
            ChipType.NINETH -> {

            }
        }
    }
}