package com.bestapp.rice.ui.meetingmanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentMeetingManagementBinding
import com.bestapp.rice.databinding.ItemMyMeetingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeetingManagementFragment : Fragment() {
    private val viewModel: MeetingManagementViewModel by viewModels()

    private var _binding: FragmentMeetingManagementBinding? = null
    private val binding
        get() = _binding!!

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

        viewModel.getMeetingByUserDocumentID()
        setObserve()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetingManagementUiState.collect {
                val (comingMeetings, endMeetings) = it.partition {
                    it.activation == true
                }

                setView(comingMeetings, endMeetings)
            }
        }
    }

    private fun setView(
        comingMeetings: List<MeetingManagementUiState>,
        endMeetings: List<MeetingManagementUiState>
    ) {
        val comingMeetingBindings = listOf(binding.itemEnableMeeting1, binding.itemEnableMeeting2, binding.itemEnableMeeting3)
        initMeetingByActivation(MeetingActivation.COMING, comingMeetingBindings, comingMeetings)

        val endMeetingBindings = listOf(binding.itemDisableMeeting1, binding.itemDisableMeeting2, binding.itemDisableMeeting3)
        initMeetingByActivation(MeetingActivation.END, endMeetingBindings, endMeetings)
    }

    /**
     *  참여중인 모임과 지난 모임을 세팅하는 함수
     *  띄워야 할 개수를 count로 계산하여 구현
     */
    private fun initMeetingByActivation(
        meetingActivation: MeetingActivation,
        meetingBindings: List<ItemMyMeetingBinding>,
        meetings: List<MeetingManagementUiState>,
    ) {
        when (meetingActivation) {
            MeetingActivation.COMING -> changeActionIcon(meetingBindings)
            MeetingActivation.END -> changeReviewUI(meetingBindings, meetings)
        }
        val count = minOf(meetings.size, meetingBindings.size)

        for (i in 0 until count) {
            meetingBindings[i].onBind(meetings[i])
        }
    }

    private fun ItemMyMeetingBinding.onBind(endMeeting: MeetingManagementUiState) {
        iv.load(endMeeting.titleImage)
        tvTitle.text = endMeeting.title
        tvLocation.text = endMeeting.placeLocation.locationAddress

        itemMyMeeting.visibility = View.VISIBLE
    }

    private fun changeActionIcon(comingMeetingBindings: List<ItemMyMeetingBinding>) {
        comingMeetingBindings.forEach { itemMyMeetingBinding ->
            itemMyMeetingBinding.ivAction.setImageResource(R.drawable.baseline_keyboard_arrow_right_24)
        }
    }

    private fun changeReviewUI(
        endMeetingBindings: List<ItemMyMeetingBinding>,
        endMeetings: List<MeetingManagementUiState>
    ) {
        val count = minOf(endMeetings.size, endMeetingBindings.size)

        for (i in 0 until count) {
            endMeetingBindings[i].switchReviewVisibility(endMeetings[i].isDoneReview)
        }
    }

    private fun ItemMyMeetingBinding.switchReviewVisibility(isDoneReview: Boolean) {
        if (isDoneReview) {
            tvReview.visibility = View.GONE
            ivAction.isEnabled = false
        } else {
            tvReview.visibility = View.VISIBLE
            ivAction.isEnabled = true
        }
    }
}