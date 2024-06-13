package com.bestapp.rice.ui.recruitment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.databinding.FragmentRecruitmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class RecruitmentFragment : Fragment() {
    private var _binding: FragmentRecruitmentBinding? = null
    private val binding: FragmentRecruitmentBinding
        get() = _binding!!

    private var chipType : String = ""
    private val recruitmentViewModel : RecruitmentViewModel by viewModels()

    private var imageResult: Uri? = null

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it) {
                transLauncher()
            } else {
                Toast.makeText(requireContext(), "권한 거부", Toast.LENGTH_SHORT).show()
            }
        }


    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val imageUri : Uri? = result.data?.data
                imageUri?.let {
                    binding.titleImage.setImageURI(it)
                    imageResult = it
                    recruitmentViewModel.getImageTrans(it)
                }
            }
        }


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
        permissionCheck()
        editTextException()
    }

    private fun initViews() {
        var hostKey : String  = ""
        var hostTemperature : Double = 0.0

        recruitmentViewModel.getDocumentId()

        recruitmentViewModel.hostInfo.observe(viewLifecycleOwner) {
            hostTemperature = it.temperature
            hostKey = it.userDocumentID
        }

        var lat : String = ""
        var lng : String = ""
        var imageValue : String = ""

        var placeLocation = PlaceLocation( //위치 값 가져오면 수정
            locationAddress = "",
            locationLat = lat,
            locationLong = lng
        )

        recruitmentViewModel.location.observe(viewLifecycleOwner) {
            //lat = it.documents[0].latitude
            //lng = it.documents[0].longitude
            if(it.documents.isEmpty()) {
                Toast.makeText(requireContext(), "주소가 올바르지 않습니다. 다시한번 확인해주세요!!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "주소가 확인 되었습니다!!", Toast.LENGTH_SHORT).show()
                placeLocation = PlaceLocation(
                    it.documents[0].addressName,
                    it.documents[0].latitude,
                    it.documents[0].longitude
                )
            }
        }

        recruitmentViewModel.imageTrans.observe(viewLifecycleOwner) {
            imageValue = it
        }

        binding.bLocation.setOnClickListener {
            recruitmentViewModel.getLocation(binding.etLocation.text.toString(), "similar")
        }

        val members: List<String> = listOf()
        val pendingMembers: List<String> = listOf()
        val attendanceCheck: List<String> = listOf()
        val activation= true

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.completeButton.setOnClickListener {
            var costTypeByPerson : String = ""
            when(binding.costEdit.text.toString().toInt()) {
                in (1..29999) -> {costTypeByPerson = "1"}
                in (30000..49999) -> {costTypeByPerson = "2"}
                in (50000..69999) -> {costTypeByPerson = "3"}
                in (70000..89999) -> {costTypeByPerson = "4"}
                else -> { Toast.makeText(requireContext(), "돈 한도를 초과합니다.", Toast.LENGTH_SHORT).show() }
            }
            val meet : Meeting = Meeting( //임시
                meetingDocumentID = "",
                title = binding.nameEdit.text.toString(),
                titleImage = imageValue,
                placeLocation = placeLocation,
                time = binding.timeEdit.text.toString(),
                recruits = binding.numberCheckEdit.text.toString().toInt(),
                description = binding.descriptionEdit.editText!!.text.toString(),
                mainMenu = chipType,
                costValueByPerson = binding.costEdit.text.toString().toInt(),
                costTypeByPerson = costTypeByPerson.toInt(),
                hostUserDocumentID =  hostKey,
                hostTemperature = hostTemperature,
                members = members,
                pendingMembers = pendingMembers,
                attendanceCheck = attendanceCheck,
                activation = activation,
            )


            recruitmentViewModel.registerMeeting(meet)
        }

        recruitmentViewModel.recruit.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "모집글 양식에 맞게 작성 해주세요!!", Toast.LENGTH_SHORT).show()
            }
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

    private fun editTextException() {

        binding.timeEdit.addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.completeButton.isClickable =
                    (binding.nameEdit.length() > 0 && binding.locationText.length() > 0
                            && binding.timeEdit.length() > 0 && binding.dateEdit.length() > 0
                            && binding.textCount.length() > 0 && binding.numberCheckEdit.length() > 0
                            && binding.costEdit.length() > 0)
                binding.completeButton.isEnabled =
                    (binding.nameEdit.length() > 0 && binding.locationText.length() > 0
                            && binding.timeEdit.length() > 0 && binding.dateEdit.length() > 0
                            && binding.textCount.length() > 0 && binding.numberCheckEdit.length() > 0
                            && binding.costEdit.length() > 0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun transLauncher() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        imageLauncher.launch(intent)
    }

    private fun permissionCheck() {
        binding.titleImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    -> {
                        transLauncher()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("권한 설정")
                            .setMessage("내부 저장소를 켜시려면 동의 버튼을 눌러주세요")
                            .setPositiveButton("동의",
                                DialogInterface.OnClickListener { _, _ ->
                                    galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                })
                            .setNegativeButton("거부",
                                DialogInterface.OnClickListener { _, _ ->
                                    Toast.makeText(context, "권한설정을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                                })
                            .show()
                    }
                    else -> {
                        galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            } else {
                when {
                    ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    -> {
                        transLauncher()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("권한 설정")
                            .setMessage("내부 저장소를 켜시려면 동의 버튼을 눌러주세요")
                            .setPositiveButton("동의",
                                DialogInterface.OnClickListener { _, _ ->
                                    galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                })
                            .setNegativeButton("거부",
                                DialogInterface.OnClickListener { _, _ ->
                                    Toast.makeText(context, "권한설정을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                                })
                            .show()
                    }
                    else -> {
                        galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
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