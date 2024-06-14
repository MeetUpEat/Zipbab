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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestapp.zipbab.databinding.FragmentInputCostBinding
import com.bestapp.zipbab.model.MeetingUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InputCostFragment : Fragment() {


    private var _binding: FragmentInputCostBinding? = null
    private val binding: FragmentInputCostBinding
        get() = _binding!!

    private val costCategoryViewModel: CostViewModel by viewModels({ requireParentFragment() })

    private val costCategoryAdapter: CostCategoryAdapter by lazy {
        CostCategoryAdapter(
            onCostCategoryClick = ::goMeeting
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputCostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCostCategoryAdapter()
        setupObserve()
    }

    private fun setupObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                costCategoryViewModel.meetingList.collect {
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

    private fun setCostCategoryAdapter() {
        val costCategoryManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rv.apply {
            layoutManager = costCategoryManager
            adapter = costCategoryAdapter
        }
    }

    private fun goMeeting(meetingUiState: MeetingUiState) {
        costCategoryViewModel.goMeeting(meetingUiState)
    }
}