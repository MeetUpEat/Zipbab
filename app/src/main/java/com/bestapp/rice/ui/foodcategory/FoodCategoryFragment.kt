package com.bestapp.rice.ui.foodcategory

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentFoodCategoryBinding
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.ui.BaseFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FoodCategoryFragment :
    BaseFragment<FragmentFoodCategoryBinding>(FragmentFoodCategoryBinding::inflate) {



    private val foodCategoryAdapter: FoodCategoryAdapter by lazy {
        FoodCategoryAdapter(
            onFoodCategoryClick = ::goMeeting,
        )
    }

    private val viewModel: FoodCategoryViewModel by viewModels {
        FoodCategoryFactory()
    }

    private val args: FoodCategoryFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFoodCategoryAdapter()
        setupView()
        setupData()
        setupListener()
        setupObserve()
    }


    private fun setupView() {
    }

    private fun setupData() {

    }

    private fun setupListener() {
        binding.tl.onTabSelected{ tab ->
            viewModel.getFoodMeeting(tab.text.toString())

        }
    }

    private fun setupObserve() {
        var selectIndex = 0
        val foodCategory = args.foodCategory as FilterUiState.FoodUiState
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.foodCategory.collect {
                    it.forEachIndexed { index, tab ->
                        val tabIcon = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_launcher_background
                        ) // 기본 아이콘
                        val request = ImageRequest.Builder(requireContext())
                            .data(tab.icon)
                            .target { drawable ->
                                val tabItem = binding.tl.newTab()
                                tabItem.setIcon(drawable)
                                tabItem.text = tab.name
                                binding.tl.addTab(tabItem)
                                if (tab.name == foodCategory.name) selectIndex = index
                            }
                            .error(tabIcon)
                            .build()

                        val imageLoader = ImageLoader.Builder(requireContext())
                            .build()

                        imageLoader.enqueue(request)
                    }
                    binding.tl.selectTab(binding.tl.getTabAt(selectIndex))
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.meetingList.collect(foodCategoryAdapter::submitList)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goMeetingNavi.collect { goMeetingNavi->
                    when(goMeetingNavi){
                        GoMeetingNavi.GO_MEETING_INFO ->{
                            val meetingDocumentId = viewModel.meetingDocumentID
                            Log.e("cyc","GO_MEETING_INFO--meetingDocumentId->${meetingDocumentId}")
                            val action = FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingInfoFragment(meetingDocumentId)
                            findNavController().navigate(action)
                        }
                        GoMeetingNavi.GO_MEETING_MANAGEMENT->{
                            val meetingDocumentId = viewModel.meetingDocumentID
                            Log.e("cyc","GO_MEETING_MANAGEMENT--meetingDocumentId->${meetingDocumentId}")
                            val action = FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingManagementFragment(meetingDocumentId)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun setFoodCategoryAdapter() {
        val foodCategoryManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
        binding.rv.apply {
            layoutManager = foodCategoryManager
            adapter = foodCategoryAdapter
        }
    }

    private fun goMeeting(meetingUiState: MeetingUiState){
        viewModel.goMeeting(meetingUiState)
    }
}