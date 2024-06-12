package com.bestapp.rice.ui.cost

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
import com.bestapp.rice.databinding.FragmentCostBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CostFragment : Fragment() {

    private var _binding: FragmentCostBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: CostViewModel by viewModels()

    private lateinit var costCategoryViewpagerAdapter: CostCategoryViewpagerAdapter


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
        setupListener()
        setupObserve()
        setupView()
    }

    private fun setupView() {
        costCategoryViewpagerAdapter =
            CostCategoryViewpagerAdapter(childFragmentManager, lifecycle)
        binding.vp.adapter = costCategoryViewpagerAdapter

        TabLayoutMediator(binding.tl, binding.vp) { tab, position ->
            viewLifecycleOwner.lifecycleScope.launch {
                tab.text = viewModel.costCategory.first()[position].name
            }
        }.attach()
    }

    private fun setupObserve() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.costCategory.collect {
                    it.ifEmpty { return@collect }

                    it.forEach { costUiState ->
                        costCategoryViewpagerAdapter.addFragment(InputCostFragment())
                    }

                    costCategoryViewpagerAdapter.notifyDataSetChanged()
                    binding.vp.setCurrentItem(viewModel.selectIndex, true)
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
                                CostFragmentDirections.actionCostFragmentToMeetingManagementFragment(
                                    goMeetingNavi.second
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun setupListener() {
        binding.tl.onTabSelected { tab ->
            if (tab.text.isNullOrEmpty()) {
                return@onTabSelected
            }
            viewModel.selectTab(tab.text.toString(), tab.position)
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        binding.vp.adapter = null
        _binding = null
        super.onDestroyView()
    }
}

