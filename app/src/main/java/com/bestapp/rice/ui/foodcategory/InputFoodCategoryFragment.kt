package com.bestapp.rice.ui.foodcategory

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
import com.bestapp.rice.databinding.FragmentInputFoodCategoryBinding
import com.bestapp.rice.model.MeetingUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InputFoodCategoryFragment : Fragment() {


    private var _binding: FragmentInputFoodCategoryBinding? = null
    private val binding: FragmentInputFoodCategoryBinding
        get() = _binding!!

    private val foodCategoryViewModel: FoodCategoryViewModel by viewModels({ requireParentFragment() })

    private val foodCategoryAdapter: FoodCategoryAdapter by lazy {
        FoodCategoryAdapter(
            onFoodCategoryClick = ::goMeeting
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputFoodCategoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFoodCategoryAdapter()
        setupObserve()
    }

    private fun setupObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                foodCategoryViewModel.meetingList.collect {
                    if (it.isEmpty()) {
                        binding.iv.isInvisible = false
                        binding.tv.isInvisible = false
                        binding.rv.isInvisible = true
                    } else {
                        binding.iv.isInvisible = true
                        binding.tv.isInvisible = true
                        binding.rv.isInvisible = false
                    }
                    foodCategoryAdapter.submitList(it)
                }
            }
        }

    }

    private fun setFoodCategoryAdapter() {
        val foodCategoryManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rv.apply {
            layoutManager = foodCategoryManager
            adapter = foodCategoryAdapter
        }
    }

    private fun goMeeting(meetingUiState: MeetingUiState) {
        foodCategoryViewModel.goMeeting(meetingUiState)
    }
}