package com.bestapp.rice.ui.recruitment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bestapp.rice.databinding.ItemRecruitBinding

class ImageSelectDialog: DialogFragment() {
    private var _binding : ItemRecruitBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ImageSelectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemRecruitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        adapter = ImageSelectAdapter()
        //adapter.submitList()
        binding.similarRecycleView.adapter = adapter

        binding.similarOk.setOnClickListener {

        }

        binding.similarCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}