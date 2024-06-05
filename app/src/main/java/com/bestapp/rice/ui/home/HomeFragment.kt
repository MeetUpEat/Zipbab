package com.bestapp.rice.ui.home

import ListItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentHomeBinding
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.ui.home.recyclerview.CostAdapter
import com.bestapp.rice.ui.home.recyclerview.FoodMenuAdapter
import com.bestapp.rice.ui.home.recyclerview.MyMeetingAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireActivity())
    }

    private val myMeetingAdapter: MyMeetingAdapter by lazy {
        MyMeetingAdapter(onMyMeetingClick = ::goMyMeet)
    }

    private val foodMenuAdapter: FoodMenuAdapter by lazy {
        FoodMenuAdapter(onFoodMenuClick = ::goFoodCategory)
    }

    private val costAdapter: CostAdapter by lazy {
        CostAdapter(onCostClick = ::goCost)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupAdapter()
        setupListener()
        setupObserve()
    }


    private fun setupAdapter() {
        setMyMeetAdapter()
        setFoodMenuAdapter()
        setCostAdapter()
    }

    private fun setupData() {
        viewModel.checkLogin()
        viewModel.getFoodCategory()
        viewModel.getCostCategory()
        viewModel.getMeetingByUserDocumentID()
    }


    private fun setupListener() {

        binding.etSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        binding.ivNotification.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNotificationFragment()
            findNavController().navigate(action)
        }

        binding.fb.setOnClickListener {
            viewModel.goNavigate()
        }
    }


    private fun setupObserve() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLogin.collect {
                    binding.tvMyMeetTitle.isVisible = it
                    binding.clMyMeet.isVisible = it
                }
            }
        }

        lifecycleScope.launch {
            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.foodCategory.collect(foodMenuAdapter::submitList)
                }
            }
            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.costCategory.collect(costAdapter::submitList)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.enterMeeting.collect(myMeetingAdapter::submitList)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goNavigate.collect {

                    when (it) {
                        MoveNavigate.GO_CREATMEET -> {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToRecruitmentFragment()
                            findNavController().navigate(action)
                        }

                        MoveNavigate.GO_LOGIN -> {
                            val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                            findNavController().navigate(action)
                        }

                        MoveNavigate.GO_NO -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goMyMeeting.collect { goMeetingNavi ->
                    when (goMeetingNavi) {
                        MoveMyMeetingNavigate.GO_MEETING_INFO -> {

                            val meetingDocumentId = viewModel.meetingDocumentID
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToMeetingInfoFragment(
                                    meetingDocumentId
                                )
                            findNavController().navigate(action)
                        }

                        MoveMyMeetingNavigate.GO_MEETING_MANAGEMENT -> {

                            val meetingDocumentId = viewModel.meetingDocumentID
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToMeetingManagementFragment(
                                    meetingDocumentId
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.enterMeeting.collect(myMeetingAdapter::submitList)
            }
        }
    }


    /**
     * My 모임 어댑터 세팅
     */
    private fun setMyMeetAdapter() {
        val myMeetingManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMyMeet.apply {
            layoutManager = myMeetingManager
            adapter = myMeetingAdapter
        }
    }

    /**
     * 음식 메뉴 어댑터 세팅
     */
    private fun setFoodMenuAdapter() {
        val foodMenuManager =
            GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
        binding.rvFoodMenu.apply {
            layoutManager = foodMenuManager
            adapter = foodMenuAdapter
        }
    }

    /**
     * Cost 어댑터 세팅
     */
    private fun setCostAdapter() {
        val costManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val density = resources.displayMetrics.density
        val itemDecoration = ListItemDecoration(density).apply {
            setPaddingValues(
                topDp = R.dimen.default_margin8,
                bottomDp = R.dimen.default_margin8,
                startDp = R.dimen.default_margin16,
                endDp = R.dimen.default_margin16
            )
        }
        binding.rvCost.apply {
            layoutManager = costManager
            adapter = costAdapter
            addItemDecoration(itemDecoration)
        }
    }

    private fun goMyMeet(meetingUiState: MeetingUiState) {
        viewModel.goMyMeeting(meetingUiState)
    }


    private fun goFoodCategory(foodCategory: FilterUiState.FoodUiState) {
        val action = HomeFragmentDirections.actionHomeFragmentToFoodCategoryFragment(foodCategory)
        findNavController().navigate(action)
    }

    private fun goCost(costCategory: FilterUiState.CostUiState) {
        val action = HomeFragmentDirections.actionHomeFragmentToCostFragment(costCategory)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.rvCost.adapter = null
        binding.rvMyMeet.adapter = null
        binding.rvFoodMenu.adapter = null
        _binding = null
        super.onDestroyView()
    }
}