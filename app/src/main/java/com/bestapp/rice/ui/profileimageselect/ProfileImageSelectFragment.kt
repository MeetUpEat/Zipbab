package com.bestapp.rice.ui.profileimageselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bestapp.rice.databinding.FragmentProfileImageSelectBinding

class ProfileImageSelectFragment : Fragment() {

    private var _binding: FragmentProfileImageSelectBinding? = null
    private val binding: FragmentProfileImageSelectBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileImageSelectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}