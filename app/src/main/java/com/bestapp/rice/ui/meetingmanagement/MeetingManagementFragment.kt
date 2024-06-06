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

    private var _comingMeetingBindings: List<ItemMyMeetingBinding>? = null
    private val comingMeetingBindings
        get() = _comingMeetingBindings!!

    private var _endMeetingBindings: List<ItemMyMeetingBinding>? = null
    private val endMeetingBindings
        get() = _endMeetingBindings!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetingManagementBinding.inflate(inflater, container, false)

        _comingMeetingBindings = listOf(binding.itemEnableMeeting1, binding.itemEnableMeeting2, binding.itemEnableMeeting3)
        _endMeetingBindings = listOf(binding.itemDisableMeeting1, binding.itemDisableMeeting2, binding.itemDisableMeeting3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        changeActionIcon()
    }

    private fun loadData() {
        viewModel.getMeetingByUserDocumentID()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetingManagementUiState.collect {
                val (comingMeetings, endMeetings) = it.partition {
                    it.activation == true
                }

                initMeetingByActivation(comingMeetingBindings, comingMeetings)
                initMeetingByActivation(endMeetingBindings, endMeetings)
            }
        }
    }

    private fun changeActionIcon() {
        comingMeetingBindings.forEach { itemMyMeetingBinding ->
            itemMyMeetingBinding.ivAction.setImageResource(R.drawable.baseline_keyboard_arrow_right_24)
        }
    }

    /**
     *  참여중인 모임과 지난 모임을 세팅하는 함수
     *  띄워야 할 개수를 count로 계산하여 구현
     */
    private fun initMeetingByActivation(
        meetingBindings: List<ItemMyMeetingBinding>,
        meetings: List<MeetingManagementUiState>,
    ) {
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

    private fun initEndMeeting(endMeetings: List<MeetingManagementUiState>) {
        // TODO: 서버에서 유저가 어떤 모임들을 평가 했는지 데이터를 받은 뒤 수정해야함
        // endMeetingBindings[0].switchReviewVisibility(true)
        // endMeetingBindings[1].switchReviewVisibility(false)
    }

    private fun ItemMyMeetingBinding.switchReviewVisibility(isDoneReview: Boolean) {
        if (isDoneReview) {
            tvReview.visibility = View.VISIBLE
            ivAction.isEnabled = true
        } else {
            tvReview.visibility = View.GONE
            ivAction.isEnabled = false
        }
    }

    override fun onDestroyView() {
        _binding = null
        _comingMeetingBindings = null
        _endMeetingBindings = null

        super.onDestroyView()
    }
}