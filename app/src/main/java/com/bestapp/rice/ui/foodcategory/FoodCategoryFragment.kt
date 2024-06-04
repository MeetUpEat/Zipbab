package com.bestapp.rice.ui.foodcategory

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.bestapp.rice.databinding.FragmentFoodCategoryBinding
import com.bestapp.rice.databinding.MenuFoodCategoryBinding
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.foodcategory.viewpager.FoodCategoryViewpagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class FoodCategoryFragment :
    BaseFragment<FragmentFoodCategoryBinding>(FragmentFoodCategoryBinding::inflate) {

    private lateinit var foodCategoryViewpagerAdapter: FoodCategoryViewpagerAdapter

    private val viewModel: FoodCategoryViewModel by viewModels {
        FoodCategoryFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupObserve()
        setupView()
    }

    private fun setupView() {
        foodCategoryViewpagerAdapter =
            FoodCategoryViewpagerAdapter(childFragmentManager, lifecycle)
        binding.vp.adapter = foodCategoryViewpagerAdapter

        TabLayoutMediator(binding.tl, binding.vp) { tab, position ->

            val mBinding = MenuFoodCategoryBinding.inflate(layoutInflater, null, false)

            lifecycleScope.launch {
                tab.text = viewModel.foodCategory.first()[position].name
                val icon = viewModel.foodCategory.first()[position].icon
                tab.customView = (createTabItemView(mBinding, tab.text.toString(), icon))
                tab.customView?.setOnClickListener {
                    mBinding.tv.isSelected = !mBinding.tv.isSelected
                    binding.tl.selectTab(tab)
                    viewModel.selectIndex = position
                }
            }
        }.attach()
    }

    private fun setupListener() {
        binding.tl.onTabSelected { tab ->
            if (tab.text.isNullOrEmpty()) {
                Log.e("cyc", "")
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

    private fun createTabItemView(
        mBinding: MenuFoodCategoryBinding,
        text: String,
        imgUri: String
    ): View {
        mBinding.iv.load(imgUri)
        mBinding.tv.text = text
        return mBinding.root
    }
}


