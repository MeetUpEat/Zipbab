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
import com.bestapp.rice.databinding.FragmentInputFoodCategoryBinding
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.ui.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InputFoodCategoryFragment: BaseFragment<FragmentInputFoodCategoryBinding>(FragmentInputFoodCategoryBinding::inflate) {



    private val foodCategoryViewModel: FoodCategoryViewModel by viewModels({ requireParentFragment() })

    private val foodCategoryAdapter: FoodCategoryAdapter by lazy {
        FoodCategoryAdapter(
            onFoodCategoryClick = ::goMeeting
        )
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

    }

    private fun setupObserve() {


//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                foodCategoryViewModel.meetingList.collect(foodCategoryAdapter::submitList)
//            }
//        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                foodCategoryViewModel.meetingList.collect{
                    Log.e("cyc","리사이클러뷰에 들어갈 데이터-->${it}")
                    foodCategoryAdapter.submitList(it)
                }
            }
        }

//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                foodCategoryViewModel.goMeetingNavi.collect { goMeetingNavi->
//                    when(goMeetingNavi){
//                        GoMeetingNavi.GO_MEETING_INFO ->{
//                            val meetingDocumentId = foodCategoryViewModel.meetingDocumentID
//                            Log.e("cyc","GO_MEETING_INFO--meetingDocumentId->${meetingDocumentId}")
//                            val action = FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingInfoFragment(meetingDocumentId)
//                            findNavController().navigate(action)
//                        }
//                        GoMeetingNavi.GO_MEETING_MANAGEMENT->{
//                            val meetingDocumentId = foodCategoryViewModel.meetingDocumentID
//                            Log.e("cyc","GO_MEETING_MANAGEMENT--meetingDocumentId->${meetingDocumentId}")
//                            val action = FoodCategoryFragmentDirections.actionFoodCategoryFragmentToMeetingManagementFragment(meetingDocumentId)
//                            findNavController().navigate(action)
//                        }
//                    }
//                }
//            }
//        }
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
        foodCategoryViewModel.goMeeting(meetingUiState)
    }


}