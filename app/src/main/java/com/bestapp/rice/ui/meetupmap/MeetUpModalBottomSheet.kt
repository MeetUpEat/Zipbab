package com.bestapp.rice.ui.meetupmap

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ModalBottomSheetsMeetingListBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeetUpModalBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: MeetUpMapViewModel by viewModels()

    private var _binding: ModalBottomSheetsMeetingListBinding? = null
    private val binding: ModalBottomSheetsMeetingListBinding
        get() = _binding!!

    val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // Do something for new state.
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Do something for slide offset.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ModalBottomSheetsMeetingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        dialog?.setOnShowListener { it ->
            val dialog = it as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                // To add the callback:
                behavior.addBottomSheetCallback(bottomSheetCallback)

                // To remove the callback:
                behavior.removeBottomSheetCallback(bottomSheetCallback)
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meetUpMapUiState.collect {
                Log.d("띄워야 할 미팅 데이터들", it.toString())
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}