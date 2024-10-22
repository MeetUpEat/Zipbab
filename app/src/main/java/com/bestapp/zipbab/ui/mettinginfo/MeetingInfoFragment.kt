package com.bestapp.zipbab.ui.mettinginfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.databinding.FragmentMeetingInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


@AndroidEntryPoint
class MeetingInfoFragment : Fragment() {

    private var _binding: FragmentMeetingInfoBinding? = null
    private val binding: FragmentMeetingInfoBinding
        get() = _binding!!


    private val viewModel: MeetingInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeetingInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserve()
        setupListener()
    }

    private fun setupListener() {
        binding.clHost.setOnClickListener {
            val action =
                MeetingInfoFragmentDirections.actionMeetingInfoFragmentToProfileFragment(viewModel.hostDocumentId)
            findNavController().navigate(action)
        }

        binding.btn.setOnClickListener {
            viewModel.btnEvent()
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    private fun setupObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meeting.collect { meetingUiState ->
                    binding.iv.load(meetingUiState.titleImage)
                    binding.tvTitle.text = meetingUiState.title
                    binding.tvPeopleCount.text = resources.getString(R.string.meeting_info_count).format(
                        meetingUiState.members.size + HOST_COUNT, meetingUiState.recruits
                    )
                    binding.tvLocation.text = String.format(
                        resources.getString(R.string.meeting_info_location),
                        meetingUiState.placeLocationUiState.locationAddress
                    )
                    binding.tvTime.text = String.format(
                        resources.getString(R.string.meeting_info_time),
                        meetingUiState.time
                    )
                    binding.tvMainMenu.text = String.format(
                        resources.getString(R.string.meeting_info_main_menu),
                        meetingUiState.mainMenu
                    )
                    binding.tvCost.text = String.format(
                        resources.getString(
                            R.string.meeting_info_cost,
                            meetingUiState.costValueByPerson
                        )
                    )
                    binding.tvContent.text = meetingUiState.description
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPossible.collect {
                    binding.btn.isEnabled = it
                    binding.btn.isClickable = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPendingPossible.collect {
                    binding.btn.isEnabled = it
                    binding.btn.isClickable = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hostUser.collect {
                    binding.ivImg.load(it.profileImage)
                    binding.tvHostName.text = it.nickname
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        Event.GO_EVENT -> {
                            val action =
                                MeetingInfoFragmentDirections.actionMeetingInfoFragmentToLoginFragment(
                                    viewModel.getMeetingDocumentId()
                                )
                            findNavController().navigate(action)
                        }

                        Event.JOIN_MEETING -> {
                            viewModel.addPendingMember()
                            viewModel.getUserArgument()

                            val hostId = viewModel.hostDocumentId
                            val meetingId = viewModel.getMeetingDocumentId()

                            viewModel.argument.observe(viewLifecycleOwner) {
                                val userId = it.first
                                val userName = it.second
                                val currentTime = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())

                                val notifyType = NotificationTypeResponse.UserResponseNotification(
                                    title = "모임 신청 알림",
                                    dec = "$userName 님이 모임 참가신청을 하였습니다.",
                                    uploadDate = currentTime,
                                    meetingDocumentId = meetingId,
                                    userDocumentId = userId
                                )

                                viewModel.addNotifyList(hostId, notifyType)
                                binding.btn.isClickable = false
                                binding.btn.isEnabled  =false
                            }

                            Toast.makeText(requireActivity(), "신청되셨습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }


//                    binding.btn.isEnabled = false
//                    delay(3000)
//                    binding.btn.isEnabled = true
                }
            }
        }
    }


    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        const val HOST_COUNT = 1
    }
}