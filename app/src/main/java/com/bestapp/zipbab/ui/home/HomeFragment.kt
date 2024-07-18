package com.bestapp.zipbab.ui.home

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
import com.bestapp.zipbab.databinding.FragmentHomeBinding
import com.bestapp.zipbab.model.toArgs
import com.bestapp.zipbab.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val foodMenuAdapter = FoodMenuAdapter { foodCategory ->
        val action =
            HomeFragmentDirections.actionHomeFragmentToFoodCategoryFragment(foodCategory.toArgs())
        action.safeNavigate(this)
    }

    private val costAdapter = CostAdapter { costCategory ->
        val action = HomeFragmentDirections.actionHomeFragmentToCostFragment(costCategory.toArgs())
        action.safeNavigate(this)
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

        setAdapter()
        setListener()
        setObserve()
    }

    private fun setAdapter() {
        binding.rvFoodMenu.adapter = foodMenuAdapter
        binding.rvCost.adapter = costAdapter
    }

    private fun setListener() {
        binding.llSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        binding.ivWrite.setOnClickListener {
            viewModel.onWrite()
        }
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userLoginState.collect { isLoggedIn ->
                        binding.ivNotification.setOnClickListener {
                            if (isLoggedIn) {
                                val action =
                                    HomeFragmentDirections.actionHomeFragmentToNotificationFragment()
                                action.safeNavigate(this@HomeFragment)
                            } else {
                                val action = HomeFragmentDirections.actionHomeFragmentToLoginGraph()
                                action.safeNavigate(this@HomeFragment)
                            }
                        }
                    }
                }
                launch {
                    viewModel.foodCategory.collect { state ->
                        foodMenuAdapter.submitList(state)
                    }
                }
                launch {
                    viewModel.costCategory.collect { state ->
                        costAdapter.submitList(state)
                    }
                }
                launch {
                    viewModel.navDestination.collect { destination ->
                        when (destination) {
                            NavDestination.Default -> Unit

                            NavDestination.Recruitment -> {
                                viewModel.onNavConsumed()

                                val action =
                                    HomeFragmentDirections.actionHomeFragmentToRecruitmentFragment()
                                action.safeNavigate(this@HomeFragment)
                            }

                            NavDestination.Login -> {
                                viewModel.onNavConsumed()

                                val action = HomeFragmentDirections.actionHomeFragmentToLoginGraph()
                                action.safeNavigate(this@HomeFragment)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvCost.adapter = null
        binding.rvFoodMenu.adapter = null
        _binding = null

        super.onDestroyView()
    }
}