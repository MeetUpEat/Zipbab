package com.bestapp.rice.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentSearchBinding
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.ui.BaseFragment
import kotlinx.coroutines.launch

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {


    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory()
    }

    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(
            onSearchClick = ::goDetailMeeting,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchAdapter()
        setupObserve()
        setupListener()
    }

    private fun setSearchAdapter() {
        val searchManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rv.apply {
            layoutManager = searchManager
            adapter = searchAdapter
        }
    }


    private fun setupObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchMeeting.collect {
                    if (it.isEmpty()) {
                        binding.rv.isInvisible = true
                        binding.tvEmpty.isInvisible = false
                        binding.ivEmpty.isInvisible = false
                    } else {
                        binding.rv.isInvisible = false
                        binding.tvEmpty.isInvisible = true
                        binding.ivEmpty.isInvisible = true
                    }
                    searchAdapter.submitList(it)
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goDirection.collect {
                    when (it) {
                        MoveDirection.GO_MEETING_MANAGEMENT -> {
                            val meetingDocumentID = viewModel.getMeetingDocumentId()
                            val action =
                                SearchFragmentDirections.actionSearchFragmentToMeetingManagementFragment(
                                    meetingDocumentID
                                )
                            findNavController().navigate(action)
                        }

                        MoveDirection.GO_MEETING_INFO -> {
                            val meetingDocumentID = viewModel.getMeetingDocumentId()
                            val action =
                                SearchFragmentDirections.actionSearchFragmentToMeetingInfoFragment(
                                    meetingDocumentID
                                )
                            findNavController().navigate(action)
                        }

                        MoveDirection.GO_LOGIN -> {
                            findNavController().navigate(R.id.action_searchFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
    }

    private fun setupListener() {
        binding.etSearch.apply {
            this.setOnEditorActionListener { textView, actionId, keyEvent ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        Log.e("cyc", "키보드 검색")
                        val processQuery = binding.etSearch.text.toString().trim()
                        viewModel.requestSearch(processQuery)
                        binding.etSearch.setText(processQuery)
                        binding.etSearch.hideSoftKeyboard()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun goDetailMeeting(meetingUiState: MeetingUiState) {
        viewModel.setMeetingDocumentId(meetingUiState.meetingDocumentID)
        viewModel.goDetailMeeting(meetingUiState)
    }

}