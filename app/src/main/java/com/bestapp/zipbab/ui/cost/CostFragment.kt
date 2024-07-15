package com.bestapp.zipbab.ui.cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.databinding.FragmentCostBinding
import com.bestapp.zipbab.model.FilterUiState
import com.bestapp.zipbab.model.MeetingUiState
import com.bestapp.zipbab.ui.cost.recyclerview.CostCategoryAdapter
import com.bestapp.zipbab.ui.cost.recyclerview.TabItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CostFragment : Fragment() {

    private var _binding: FragmentCostBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: CostViewModel by viewModels()

    private val costCategoryAdapter: CostCategoryAdapter by lazy {
        CostCategoryAdapter(
            onCostCategoryClick = ::goMeeting
        )
    }
    private val tabItemAdapter: TabItemAdapter by lazy {
        TabItemAdapter(
            onCostTabItemClick = ::onClickTab
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setupListener()
        setupObserve()
    }

    private fun setupListener() {
        binding.mt.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setupObserve() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.costCategory.collect {
                    tabItemAdapter.setSelectIndex(viewModel.getSelectIndex())
                    tabItemAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goMeetingNavi.collect { goMeetingNavi ->
                    when (goMeetingNavi.first) {
                        MoveMeetingNavi.GO_MEETING_INFO -> {
                            val action =
                                CostFragmentDirections.actionCostFragmentToMeetingInfoFragment(
                                    goMeetingNavi.second
                                )
                            findNavController().navigate(action)
                        }

                        MoveMeetingNavi.GO_MEETING_MANAGEMENT -> {
                            val action =
                                CostFragmentDirections.actionCostFragmentToMeetingInfoFragment(
                                    goMeetingNavi.second
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meetingList.collect {
                    if (it.isEmpty()) {
                        binding.iv.isInvisible = false
                        binding.tv.isInvisible = false
                        binding.rv.isInvisible = true
                    } else {
                        binding.iv.isInvisible = true
                        binding.tv.isInvisible = true
                        binding.rv.isInvisible = false
                    }
                    costCategoryAdapter.submitList(it)
                }
            }
        }
    }


    private fun setAdapter() {
        binding.rv.apply {
            adapter = costCategoryAdapter
        }

        binding.rvTl.apply {
            adapter = tabItemAdapter
        }
    }

    private fun goMeeting(meetingUiState: MeetingUiState) {
        viewModel.goMeeting(meetingUiState)
    }

    private fun onClickTab(costUiState: FilterUiState.CostUiState, position: Int) {
        viewModel.setSelectIndex(position)
        viewModel.getCostMeeting(costUiState.type)
    }

    override fun onDestroyView() {
        binding.rv.adapter = null
        binding.rvTl.adapter = null
        _binding = null
        super.onDestroyView()
    }
}


