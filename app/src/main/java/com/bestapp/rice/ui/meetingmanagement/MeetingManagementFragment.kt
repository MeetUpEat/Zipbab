package com.bestapp.rice.ui.meetingmanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentMeetingManagementBinding
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.launch

class MeetingManagementFragment : Fragment() {

    private var _binding: FragmentMeetingManagementBinding? = null
    private val binding: FragmentMeetingManagementBinding
        get() = _binding!!

    private val viewModel: MeetingManagementViewModel by viewModels()

    private val meetingMemberAdapter by lazy {
        MeetingMemberAdapter(onMemberClick = ::goProfile)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    binding.tvPeopleCount.text = String.format(
                        resources.getString(R.string.meeting_management_people_count),
                        meetingUiState.members.size
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
                            meetingUiState.costTypeByPerson
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
                        }

                        MoveNavigation.GO_LOGIN -> {
                            findNavController().navigate(R.id.action_meetingInfoFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect(meetingMemberAdapter::submitList)
            }
        }
    }

    private fun setupListener() {
        binding.btnMeetingEnd.setOnClickListener {
            viewModel.bottomBtnEvent(MoveNavigation.END_MEETING)
        }
        binding.btnMyGroupLocation.setOnClickListener {
            viewModel.bottomBtnEvent(MoveNavigation.GO_GROUP_LOCATION)
        }
        binding.clHost.setOnClickListener {
            val action =
                MeetingManagementFragmentDirections.actionMeetingManagementFragmentToProfileFragment(
                    viewModel.hostDocumentId
                )
            findNavController().navigate(action)
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
        _binding = null
        binding.rv.adapter = null
        super.onDestroyView()
    }


}