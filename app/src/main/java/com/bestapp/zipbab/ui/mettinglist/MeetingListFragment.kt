package com.bestapp.zipbab.ui.mettinglist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.FragmentMeetingListBinding
import com.bestapp.zipbab.databinding.ItemMyMeetingBinding
import com.bestapp.zipbab.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeetingListFragment : Fragment() {
    private val viewModel: MeetingListViewModel by viewModels()

    private var _binding: FragmentMeetingListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLoadData()

        initBackButton()
        setObserve()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initBackButton() {
        binding.mt.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObserve() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.meetingListUiState.collectLatest {
            val (comingMeetingListUis, endMeetingListUis) =
                it.meetingUis.partition { meetingUi ->
                    meetingUi.activation
                }

            setView(comingMeetingListUis, endMeetingListUis)
        }
    }

    private fun setView(
        comingMeetingUiState: List<MeetingListUi>,
        endMeetingUiState: List<MeetingListUi>,
    ) {
        val comingMeetingBindings = listOf(
            binding.itemEnableMeeting1, binding.itemEnableMeeting2, binding.itemEnableMeeting3
        )
        setMeetingByActivation(
            meetingActivationStatus = MeetingActivationStatus.COMING,
            meetingItemBindings = comingMeetingBindings,
            meetingListUis = comingMeetingUiState
        )

        val endMeetingBindings = listOf(
            binding.itemDisableMeeting1, binding.itemDisableMeeting2, binding.itemDisableMeeting3
        )
        setMeetingByActivation(
            meetingActivationStatus = MeetingActivationStatus.END,
            meetingItemBindings = endMeetingBindings,
            meetingListUis = endMeetingUiState
        )
    }

    /**
     *  참여중인 모임과 지난 모임을 세팅하는 함수
     */
    private fun setMeetingByActivation(
        meetingActivationStatus: MeetingActivationStatus,
        meetingItemBindings: List<ItemMyMeetingBinding>,
        meetingListUis: List<MeetingListUi>,
    ) {
        val showItemCount = minOf(meetingListUis.size, meetingItemBindings.size)
        Log.e("count", showItemCount.toString())

        if (showItemCount == 0) {
            return
        }

        for (i in 0 until showItemCount) {
            meetingItemBindings[i].onBind(meetingListUis[i])
        }

        when (meetingActivationStatus) {
            MeetingActivationStatus.COMING -> {
                changeActionIcon(meetingItemBindings)

                for (i in 0 until showItemCount) {
                    val meetingDocumentId = meetingListUis[i].meetingDocumentID

                    meetingItemBindings[i].ivAction.setOnClickListener {
                        goMeetingInfo(meetingDocumentId)
                    }
                }
            }

            MeetingActivationStatus.END -> {
                changeReviewUI(meetingItemBindings, meetingListUis)

                for (i in 0 until showItemCount) {
                    val clickAreas =
                        listOf(meetingItemBindings[i].tvReview, meetingItemBindings[i].ivAction)

                    clickAreas.forEach { view ->
                        view.setOnClickListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.not_yet_implemented),
                                Toast.LENGTH_SHORT
                            ).show()
                            // goReview(meetingListUis[i])
                        }
                    }
                }
            }
        }
    }

    private fun goMeetingInfo(meetingDocumentID: String) {
        val action =
            MeetingListFragmentDirections.actionMeetingListFragmentToMeetingInfoFragment(
                meetingDocumentID
            )
        action.safeNavigate(this)
    }

    private fun goReview(endMeetingListUi: MeetingListUi) {
        val action =
            MeetingListFragmentDirections.actionMeetingListFragmentToReviewFragment(endMeetingListUi.toMeetingUi())
        findNavController().navigate(action)
    }

    private fun ItemMyMeetingBinding.onBind(meetingListUi: MeetingListUi) {
        iv.load(meetingListUi.titleImage)
        iv.clipToOutline = true

        tvTitle.text = meetingListUi.title
        tvLocation.text = meetingListUi.placeLocationArgs.locationAddress

        itemMyMeeting.visibility = View.VISIBLE
    }

    private fun changeActionIcon(
        comingMeetingBindings: List<ItemMyMeetingBinding>,
    ) {
        comingMeetingBindings.forEach { itemMyMeetingBinding ->
            itemMyMeetingBinding.ivAction.setImageResource(R.drawable.baseline_keyboard_arrow_right_24)
        }
    }

    private fun changeReviewUI(
        endMeetingBindings: List<ItemMyMeetingBinding>,
        endMeetingListUis: List<MeetingListUi>,
    ) {
        val showItemCount = minOf(endMeetingListUis.size, endMeetingBindings.size)

        for (i in 0 until showItemCount) {
            val isDoneReview = endMeetingListUis[i].isDoneReview
            endMeetingBindings[i].switchReviewVisibility(isDoneReview)
        }
    }

    private fun ItemMyMeetingBinding.switchReviewVisibility(
        isDoneReview: Boolean,
    ) {
        if (isDoneReview) {
            tvReview.visibility = View.GONE
            ivAction.isEnabled = false
        } else {
            tvReview.visibility = View.VISIBLE
            ivAction.isEnabled = true
        }
    }
}