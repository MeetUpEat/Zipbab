package com.bestapp.rice.ui.term

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bestapp.rice.databinding.FragmentTermBinding

class TermFragment : Fragment() {
    private var _binding: FragmentTermBinding? = null
    private val binding: FragmentTermBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}