package com.bestapp.zipbab.ui.meetingmanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentMeetingManagementBinding
import com.bestapp.zipbab.model.UserUiState
import com.bestapp.zipbab.ui.mettinginfo.MeetingInfoFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeetingManagementFragment : Fragment() {
    private var _binding: FragmentMeetingManagementBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: MeetingManagementViewModel by viewModels()

    private val meetingMemberAdapter by lazy {
        MeetingMemberAdapter(onMemberClick = ::goProfile)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetingManagementBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMeetingMemberAdapter()
        setupListener()
        setupObserve()

    }

    private fun setupObserve() {
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
                viewModel.meeting.collect { meetingUiState ->
                    binding.iv.load(meetingUiState.titleImage)
                    binding.tvTitle.text = meetingUiState.title
                    binding.tvPeopleCount.text = resources.getString(R.string.meeting_management_people_count).format(
                        meetingUiState.members.size + MeetingInfoFragment.HOST_COUNT, meetingUiState.recruits
                    )
                    binding.tvLocation.text = String.format(
                        resources.getString(R.string.meeting_management_location),
                        meetingUiState.placeLocationUiState.locationAddress
                    )
                    binding.tvTime.text = String.format(
                        resources.getString(R.string.meeting_management_time),
                        meetingUiState.time
                    )
                    binding.tvMainMenu.text = String.format(
                        resources.getString(R.string.meeting_management_main_menu),
                        meetingUiState.mainMenu
                    )
                    binding.tvCost.text = String.format(
                        resources.getString(
                            R.string.meeting_management_cost,
                            meetingUiState.costValueByPerson
                        )
                    )
                    binding.tvContent.text = meetingUiState.description
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it.first) {
                        MoveNavigation.GO_GROUP_LOCATION -> {
                            val action =
                                MeetingManagementFragmentDirections.actionMeetingManagementFragmentToMemberLocationTrackingFragment(
                                    it.second
                                )
                            findNavController().navigate(action)
                        }

                        MoveNavigation.END_MEETING -> {
                            viewModel.endMeeting()
                            binding.btnMeetingEnd.isEnabled = false
                        }

                        MoveNavigation.GO_LOGIN -> {
                            findNavController().navigate(R.id.action_meetingManagementFragment_to_loginGraph)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { userUiStateList ->
                    if (userUiStateList.isEmpty()) {
                        binding.rv.isInvisible = true
                        binding.tvEmpty.isInvisible = false
                    } else {
                        binding.rv.isInvisible = false
                        binding.tvEmpty.isInvisible = true
                    }
                    meetingMemberAdapter.submitList(userUiStateList)
                }
            }
        }
    }

    private fun setupListener() {
        binding.btnMeetingEnd.setOnClickListener {
            viewModel.bottomBtnEvent(MoveNavigation.END_MEETING)
        }
        binding.btnMyGroupLocation.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT).show()
            // viewModel.bottomBtnEvent(MoveNavigation.GO_GROUP_LOCATION)
        }
        binding.clHost.setOnClickListener {
            val action =
                MeetingManagementFragmentDirections.actionMeetingManagementFragmentToProfileFragment(
                    viewModel.hostDocumentId
                )
            findNavController().navigate(action)
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }


    private fun goProfile(userUiState: UserUiState) {
        val action =
            MeetingManagementFragmentDirections.actionMeetingManagementFragmentToProfileFragment(
                userUiState.userDocumentID
            )
        findNavController().navigate(action)
    }


    private fun setMeetingMemberAdapter() {
        val memberManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rv.apply {
            layoutManager = memberManager
            adapter = meetingMemberAdapter
        }
    }

    override fun onDestroyView() {
        binding.rv.adapter = null
        _binding = null
        super.onDestroyView()
    }


}