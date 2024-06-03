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
import coil.ImageLoader
import coil.request.ImageRequest
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentFoodCategoryBinding
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.foodcategory.viewpager.FoodCategoryViewpagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class FoodCategoryFragment :
    BaseFragment<FragmentFoodCategoryBinding>(FragmentFoodCategoryBinding::inflate) {


//    private val foodCategoryViewpagerAdapter: FoodCategoryViewpagerAdapter by lazy {
//        FoodCategoryViewpagerAdapter(childFragmentManager, lifecycle, List<FilterUiState.FoodUiState>)
//    }

    private lateinit var foodCategoryViewpagerAdapter: FoodCategoryViewpagerAdapter

    private val viewModel: FoodCategoryViewModel by viewModels {
        FoodCategoryFactory()
    }

    private val args: FoodCategoryFragmentArgs by navArgs()


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
        Log.e("cyc","setupData")
        val foodCategory = args.foodCategory as FilterUiState.FoodUiState
        viewModel.setSelectMenu(foodCategory.name)
    }

    private fun setupListener() {
        binding.tl.onTabSelected { tab ->
            Log.e("cyc","탭레이아웃이 클리되었을 때")
            Log.e("cyc","tab.text-->${tab.text.toString()}")
            viewModel.getFoodMeeting(tab.text.toString())

        }
    }

    private fun setupObserve() {

        var selectIndex = 0
//        val foodCategory = args.foodCategory as FilterUiState.FoodUiState

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.foodCategory.collect {
                    it.ifEmpty { return@collect }

                    it.forEachIndexed { index, foodUiState ->
                        foodCategoryViewpagerAdapter.addFragment(InputFoodCategoryFragment())
                        if (foodUiState.name == viewModel.getSelectMenu()) {
//                        if (foodUiState.name == foodCategory.name) {
                            selectIndex = index
                        }
                    }
                    // inline 해당 지역 return 확인 해보기
                    foodCategoryViewpagerAdapter.notifyDataSetChanged()
                    TabLayoutMediator(binding.tl, binding.vp) { tab, position ->
                        val tabIcon = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_launcher_background
                        )
                        val request = ImageRequest.Builder(requireContext())
                            .data(it[position].icon)
                            .target { drawable ->
                                tab.setIcon(drawable)
                                tab.text = it[position].name
                            }
                            .error(tabIcon)
                            .build()

                        val imageLoader = ImageLoader.Builder(requireContext())
                            .crossfade(true)
                            .build()
                        imageLoader.enqueue(request)
                    }.attach()

                    binding.vp.setCurrentItem(selectIndex,true)
                    viewModel.getFoodMeeting(it[selectIndex].name)
                }
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
}


//package com.bestapp.rice.ui.foodcategory
//
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import androidx.recyclerview.widget.LinearLayoutManager
//import coil.ImageLoader
//import coil.request.ImageRequest
//import com.bestapp.rice.R
//import com.bestapp.rice.databinding.FragmentFoodCategoryBinding
//import com.bestapp.rice.model.FilterUiState
//import com.bestapp.rice.model.MeetingUiState
//import com.bestapp.rice.ui.BaseFragment
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//
//class FoodCategoryFragment :
//    BaseFragment<FragmentFoodCategoryBinding>(FragmentFoodCategoryBinding::inflate) {
//
//
//
//    private val foodCategoryAdapter: FoodCategoryAdapter by lazy {
//        FoodCategoryAdapter(
//            onFoodCategoryClick = ::goMeeting,
//        )
//    }
//
//    private val viewModel: FoodCategoryViewModel by viewModels {
//        FoodCategoryFactory()
//    }
//
//    private val args: FoodCategoryFragmentArgs by navArgs()
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setFoodCategoryAdapter()
//        setupView()
//        setupData()
//        setupListener()
//        setupObserve()
//    }
//
//
//    private fun setupView() {
//
//    }
//
//    private fun setupData() {
//
//    }
//
//    private fun setupListener() {
//        binding.tl.onTabSelected{ tab ->
//            viewModel.getFoodMeeting(tab.text.toString())
//
//        }
//    }
//
//    private fun setupObserve() {
//        var selectIndex = 0
//        val foodCategory = args.foodCategory as FilterUiState.FoodUiState
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.foodCategory.collect {
//                    binding.tl.removeAllTabs()
//                    Log.e("cyc","Fragment-foodCategory->${it}")
//                    it.forEachIndexed { index, tab ->
//                        Log.e("cyc","Fragment-index->${index}")
//                        Log.e("cyc","Fragment-tab->${tab}")
//                        val tabIcon = ContextCompat.getDrawable(
//                            requireContext(),
//                            R.drawable.ic_launcher_background
//                        ) // 기본 아이콘
//                        val request = ImageRequest.Builder(requireContext())
//                            .data(tab.icon)
//                            .target { drawable ->
//                                val tabItem = binding.tl.newTab()
//                                tabItem.setIcon(drawable)
//                                tabItem.text = tab.name
//                                binding.tl.addTab(tabItem)
//                                Log.e("cyc","tab.name-->${tab.name}")
//                                Log.e("cyc","foodCategory.name-->${foodCategory.name}")
//
//                                if (tab.name == foodCategory.name) {
//                                    Log.e("cyc","if가 돌아가나")
//                                    selectIndex = index
//                                    Log.e("cyc","내부 selectIndex-->")
//                                    binding.tl.getTabAt(selectIndex)?.select()
////                                    binding.tl.selectTab(binding.tl.)
//
//                                }
//                                Log.e("cyc","타겟 내부")
//                            }
//                            .error(tabIcon)
//                            .build()
//
//                        val imageLoader = ImageLoader.Builder(requireContext())
//                            .build()
//
//                        imageLoader.enqueue(request)
//                    }
//                    Log.e("cyc","selectTab -> ${selectIndex}")
////                    Log.e("cyc","selectTab -> ${selectIndex}")
//
//                }
//            }
//        }
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewModel.meetingList.collect(foodCategoryAdapter::submitList)
//            }
//        }
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewModel.goMeetingNavi.collect { goMeetingNavi->
//                    when(goMeetingNavi){
//                        GoMeetingNavi.GO_MEETING_INFO ->{
//                            val meetingDocumentId = viewModel.meetingDocumentID
//                            Log.e("cyc","GO_MEETING_INFO--meetingDocumentId->${meetingDocumentId}")
//                            val action = FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingInfoFragment(meetingDocumentId)
//                            findNavController().navigate(action)
//                        }
//                        GoMeetingNavi.GO_MEETING_MANAGEMENT->{
//                            val meetingDocumentId = viewModel.meetingDocumentID
//                            Log.e("cyc","GO_MEETING_MANAGEMENT--meetingDocumentId->${meetingDocumentId}")
//                            val action = FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingManagementFragment(meetingDocumentId)
//                            findNavController().navigate(action)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun setFoodCategoryAdapter() {
//        val foodCategoryManager =
//            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
//        binding.rv.apply {
//            layoutManager = foodCategoryManager
//            adapter = foodCategoryAdapter
//        }
//    }
//
//    private fun goMeeting(meetingUiState: MeetingUiState){
//        viewModel.goMeeting(meetingUiState)
//    }
//}
//


