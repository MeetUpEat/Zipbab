package com.bestapp.rice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<V : ViewBinding>(
    private val viewBindingInflater: (
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) -> V
) : Fragment() {

    private var _binding: V? = null
    protected val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = viewBindingInflater(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}