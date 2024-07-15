package com.bestapp.zipbab.ui.mettinginfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentMeetingInfoBinding
import com.bestapp.zipbab.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MeetingInfoFragment : Fragment() {

    private var _binding: FragmentMeetingInfoBinding? = null
    private val binding: FragmentMeetingInfoBinding
        get() = _binding!!

    private val args: MeetingInfoFragmentArgs by navArgs()

    private val viewModel: MeetingInfoViewModel by viewModels()

    private val meetingMemberAdapter = MeetingMemberAdapter { member ->
        val action =
            MeetingInfoFragmentDirections.actionMeetingInfoFragmentToProfileFragment(member.userDocumentID)
        action.safeNavigate(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setMeetingDocumentId(args.meetingDocumentId)
    }

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

        setAdapter()
        setListener()
        setObserve()
    }

    private fun setAdapter() {
        binding.rvMember.adapter = meetingMemberAdapter
    }

    private fun setListener() {
        binding.btnRegister.setOnClickListener {
            viewModel.onRegister()
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
        binding.btnMyGroupLocation.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.not_yet_implemented),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnMeetingEnd.setOnClickListener {
            viewModel.onMeetingEnd()
        }
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isHostMeeting.collect { isHost ->
                        binding.btnMeetingEnd.isVisible = isHost
                        binding.btnMyGroupLocation.isVisible = isHost
                        binding.btnRegister.isVisible = isHost.not()
                    }
                }
                launch {
                    viewModel.meeting.collect { meetingUiState ->
                        binding.ivMeetingImage.load(meetingUiState.titleImage)
                        binding.tvTitle.text = meetingUiState.title
                        binding.tvPeopleCount.text =
                            resources.getString(R.string.meeting_info_count).format(
                                meetingUiState.members.size + HOST_COUNT, meetingUiState.recruits
                            )
                        binding.tvLocation.text =
                            meetingUiState.placeLocationUiState.locationAddress
                        binding.tvTime.text = meetingUiState.time
                        binding.tvMainMenu.text = meetingUiState.mainMenu
                        binding.tvCost.text = String.format(
                            resources.getString(
                                R.string.meeting_info_cost,
                                meetingUiState.costValueByPerson
                            )
                        )
                        binding.tvContent.text = meetingUiState.description
                    }
                }
                launch {
                    viewModel.registerState.collect { state ->
                        when (state) {
                            RegisterState.Joined -> {
                                binding.btnRegister.isEnabled = false
                                binding.btnRegister.text =
                                    getString(R.string.meeting_already_joined)
                            }

                            RegisterState.NotYet -> {
                                binding.btnRegister.isEnabled = true
                                binding.btnRegister.text = getString(R.string.meeting_register)
                            }

                            RegisterState.Requested -> {
                                binding.btnRegister.isEnabled = false
                                binding.btnRegister.text =
                                    getString(R.string.meeting_already_requested)
                            }
                        }
                    }
                }
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            MeetingInfoEvent.Default -> Unit
                            MeetingInfoEvent.RegisterFail -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.meeting_request_register_fail),
                                    Toast.LENGTH_SHORT
                                ).show()

                                viewModel.onEventConsumed()
                            }

                            MeetingInfoEvent.RegisterSuccess -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.meeting_request_register_success),
                                    Toast.LENGTH_SHORT
                                ).show()

                                viewModel.onEventConsumed()
                            }

                            MeetingInfoEvent.RegisterInProgress -> {
                                Unit
                            }

                            MeetingInfoEvent.EndMeetingFail -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.meeting_end_fail), Toast.LENGTH_SHORT
                                ).show()
                            }

                            MeetingInfoEvent.EndMeetingSuccess -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.meeting_end_success), Toast.LENGTH_SHORT
                                ).show()
                                if (!findNavController().popBackStack()) {
                                    requireActivity().finish()
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.members.collect { members ->
                        meetingMemberAdapter.submitList(members)
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        binding.rvMember.adapter = null
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val HOST_COUNT = 1
    }
}