package com.bestapp.zipbab.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bestapp.zipbab.databinding.FragmentSearchBinding
import com.bestapp.zipbab.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private val searchAdapter = SearchAdapter { state ->
        val action =
            SearchFragmentDirections.actionSearchFragmentToMeetingInfoFragment(state.meetingDocumentID)
        action.safeNavigate(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setListener()
        setObserve()
    }

    private fun setAdapter() {
        binding.rvSearchResult.adapter = searchAdapter
    }

    private fun setListener() {
        binding.ibBack.setOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                val processQuery = binding.etSearch.text.toString().trim()
                viewModel.requestSearch(processQuery)
                binding.etSearch.setText(processQuery)

                hideInput()
                binding.vDummyForRemoveFocus.requestFocus()
                true
            } else {
                false
            }
        }
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val processQuery = binding.etSearch.text.toString().trim()
                viewModel.requestSearch(processQuery)
                binding.etSearch.setText(processQuery)

                hideInput()
                binding.vDummyForRemoveFocus.requestFocus()
                true
            } else {
                false
            }
        }
    }

    private fun hideInput() {
        ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(
                binding.root.windowToken,
                0
            )
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchMeeting.collect { state ->
                    binding.rvSearchResult.isVisible = state.isNotEmpty()
                    binding.tvEmpty.isVisible = state.isEmpty()
                    binding.ivEmpty.isVisible = state.isEmpty()

                    searchAdapter.submitList(state)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvSearchResult.adapter = null
        _binding = null

        super.onDestroyView()
    }
}