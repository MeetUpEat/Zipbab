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
import coil.ImageLoader
import coil.request.ImageRequest
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentFoodCategoryBinding
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.foodcategory.viewpager.FoodCategoryViewpagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch


class FoodCategoryFragment :
    BaseFragment<FragmentFoodCategoryBinding>(FragmentFoodCategoryBinding::inflate) {

    private lateinit var foodCategoryViewpagerAdapter: FoodCategoryViewpagerAdapter

    private val viewModel: FoodCategoryViewModel by viewModels {
        FoodCategoryFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupListener()
        setupObserve()
        setupView()
    }

    private fun setupView() {
        foodCategoryViewpagerAdapter =
            FoodCategoryViewpagerAdapter(childFragmentManager, lifecycle)
        binding.vp.adapter = foodCategoryViewpagerAdapter

    }

    private fun setupData() {

    }

    private fun setupListener() {
        binding.tl.onTabSelected { tab ->
            if (tab.text.isNullOrEmpty()) {
                return@onTabSelected
            }
            viewModel.selectMenu = tab.text.toString()
            viewModel.getFoodMeeting(viewModel.selectMenu)
        }
    }

    private fun setupObserve() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.foodCategory.collect {
                    it.ifEmpty { return@collect }

                    it.forEachIndexed { index, foodUiState ->
                        foodCategoryViewpagerAdapter.addFragment(InputFoodCategoryFragment())
                    }
                    // tablayout
                    // inline 해당 지역 return 확인 해보기
                    foodCategoryViewpagerAdapter.notifyDataSetChanged()
                    TabLayoutMediator(binding.tl, binding.vp) { tab, position ->
                        tab.text = it[position].name
                        val tabIcon = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_launcher_background
                        )
                        // 해당 이미지를 tab Icon에 넣는 작업
                        val request = ImageRequest.Builder(requireContext())
                            .data(it[position].icon)
                            .target { drawable ->
                                Log.e("cyc","drawable-->${drawable}")
                                tab.setIcon(drawable)
                            }
                            .error(tabIcon)
                            .build()

                        //로드되는 Builder
                        val imageLoader = ImageLoader.Builder(requireContext())
                            .crossfade(true)
                            .build()
                        imageLoader.enqueue(request)
                        Log.e("cyc","로드 완료")
                    }.attach()
                    binding.vp.setCurrentItem(viewModel.selectIndex, true)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goMeetingNavi.collect { goMeetingNavi ->
                    when (goMeetingNavi) {
                        GoMeetingNavi.GO_MEETING_INFO -> {
                            val meetingDocumentId = viewModel.meetingDocumentID
                            val action =
                                FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingInfoFragment(
                                    meetingDocumentId
                                )
                            findNavController().navigate(action)
                        }

                        GoMeetingNavi.GO_MEETING_MANAGEMENT -> {
                            val meetingDocumentId = viewModel.meetingDocumentID
                            val action =
                                FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingManagementFragment(
                                    meetingDocumentId
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }
}


