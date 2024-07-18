package com.bestapp.zipbab.ui.foodcategory

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
import com.bestapp.zipbab.databinding.FragmentFoodCategoryBinding
import com.bestapp.zipbab.model.FilterUiState
import com.bestapp.zipbab.model.MeetingUiState
import com.bestapp.zipbab.ui.foodcategory.recyclerview.FoodCategoryAdapter
import com.bestapp.zipbab.ui.foodcategory.recyclerview.TabItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodCategoryFragment : Fragment() {

    private var _binding: FragmentFoodCategoryBinding? = null
    private val binding: FragmentFoodCategoryBinding
        get() = _binding!!


    private val viewModel: FoodCategoryViewModel by viewModels()

    private val foodCategoryAdapter: FoodCategoryAdapter by lazy {
        FoodCategoryAdapter(
            onFoodCategoryClick = ::goMeeting
        )
    }

    private val tabItemAdapter: TabItemAdapter by lazy {
        TabItemAdapter(
            onFoodTabItemClick = ::onClickTab
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodCategoryBinding.inflate(inflater, container, false)

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
                viewModel.foodCategory.collect {
                    tabItemAdapter.setSelectIndex(viewModel.getSelectIndex())
                    tabItemAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.scrollEvent.collect{foodCategoryEvent ->
                    when(foodCategoryEvent){
                        FoodCategoryEvent.ScrollEvent -> {
                            binding.rvTl.smoothScrollToPosition(viewModel.getSelectIndex())
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
                    foodCategoryAdapter.submitList(it)
                }
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goMeetingNavi.collect { goMeetingNavi ->
                    when (goMeetingNavi.first) {
                        MoveMeetingNavi.GO_MEETING_INFO -> {
                            val action =
                                FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingInfoFragment(
                                    goMeetingNavi.second
                                )
                            findNavController().navigate(action)
                        }

                        MoveMeetingNavi.GO_MEETING_MANAGEMENT -> {
                            val action =
                                FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingInfoFragment(
                                    goMeetingNavi.second
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        binding.rv.apply {
            adapter = foodCategoryAdapter
        }

        binding.rvTl.apply {
            adapter = tabItemAdapter
        }

    }

    private fun goMeeting(meetingUiState: MeetingUiState) {
        viewModel.goMeeting(meetingUiState)
    }

    private fun onClickTab(foodUiState: FilterUiState.FoodUiState, position: Int) {
        //해당 ViewModel에 데이터를 저장하는 이유 열리 앱이 오래 되어 Kill Process(메모리 관리를 위해서 오래된 앱을 강제 종료)가 되어도 해당 앱의 상태값을 유지하기 위해서
        //그러기위해서는 해당 savehandle에 저장을 해야되기 때문입니다.
        //그렇기 때문에 ViewModel의 변수값을 직접 참조하여 바꾸는 것이 아닌 함수로 처리해서 savehandle저장 및 변경 등의 추가 처리를 하는 것이다.
        viewModel.setSelectIndex(position)
        viewModel.getFoodMeeting(foodUiState.name)
    }

    override fun onDestroyView() {
        binding.rv.adapter = null
        binding.rvTl.adapter = null
        _binding = null
        super.onDestroyView()
    }
}


