package com.bestapp.rice.ui.recruitment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.databinding.FragmentRecruitmentBinding
import com.bestapp.rice.ui.profile.ProfileFragmentArgs
import com.bestapp.rice.ui.profile.ProfileUiState
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class RecruitmentFragment : Fragment() {
    private var _binding: FragmentRecruitmentBinding? = null
    private val binding: FragmentRecruitmentBinding
        get() = _binding!!

    private lateinit var chipType : String
    private val recruitmentViewModel : RecruitmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecruitmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        selectLister()
    }

    private fun initViews() {
        val placeLocation = PlaceLocation( //위치 값 가져오면 수정
            locationAddress = "",
            locationLat = "",
            locationLong = ""
        )
        val members: List<String> = listOf()
        val pendingMembers: List<String> = listOf()
        val attendanceCheck: List<String> = listOf()
        val activation= true

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.titleImage.setOnClickListener {
            //TODO 사진가져오는로직
        }

        binding.completeButton.setOnClickListener {
            val meet : Meeting = Meeting( //임시
                meetingDocumentID = "",
                title = binding.nameEdit.text.toString(),
                titleImage = "",
                placeLocation = placeLocation,
                time = binding.timeEdit.text.toString(),
                recruits = binding.numberCheckEdit.text.toString().toInt(),
                description = binding.descriptionEdit.editText!!.text.toString(),
                mainMenu = chipType,
                costValueByPerson = binding.costEdit.text.toString().toInt(),
                costTypeByPerson = binding.costEdit.text.toString().toInt(), //추후수정
                host =  "", //값 가져와선 넣어주기
                hostTemperature = 0.0, //값가져와서 넣어주기
                members = members,
                pendingMembers = pendingMembers,
                attendanceCheck = attendanceCheck,
                activation = activation,
            )


            //recruitmentViewModel.registerMeeting(meet)
            Toast.makeText(context, "이기능은 미구현 상태입니다.", Toast.LENGTH_SHORT).show()
        }

        recruitmentViewModel.recruit.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "모집글 양식에 맞게 작성 해주세요!!", Toast.LENGTH_SHORT).show()
            }
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
            val timePicker = TimePickerDialog.OnTimeSetListener { _, h, m ->
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
            when (chipType) {
                R.id.first_type -> {
                    chipGroupType(ChipType.FIRST)
                }

                R.id.second_type -> {
                    chipGroupType(ChipType.SECOND)
                }

                R.id.third_type -> {
                    chipGroupType(ChipType.THIRD)
                }

                R.id.fourth_type -> {
                    chipGroupType(ChipType.FOURTH)
                }

                R.id.fifth_type -> {
                    chipGroupType(ChipType.FIFTH)
                }

                R.id.sixth_type -> {
                    chipGroupType(ChipType.SIXTH)
                }

                R.id.seventh_type -> {
                    chipGroupType(ChipType.SEVENTH)
                }

                R.id.eighth_type -> {
                    chipGroupType(ChipType.EIGHTH)
                }

                R.id.nineth_type -> {
                    chipGroupType(ChipType.NINETH)
                }
            }
        }
    }

    private fun chipGroupType(type: ChipType) {
        when (type) {
            ChipType.FIRST -> {
                chipType = "파스타"
            }

            ChipType.SECOND -> {
                chipType = "찌개"
            }

            ChipType.THIRD -> {
                chipType = "백반"
            }

            ChipType.FOURTH -> {
                chipType = "구이"
            }

            ChipType.FIFTH -> {
                chipType = "떡볶이"
            }

            ChipType.SIXTH -> {
                chipType = "샌드위치"
            }

            ChipType.SEVENTH -> {
                chipType = "베이커리"
            }

            ChipType.EIGHTH -> {
                chipType = "전"
            }

            ChipType.NINETH -> {
                chipType = "기타"
            }
        }
    }
}