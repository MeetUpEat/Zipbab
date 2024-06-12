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
class MeetUpModalBottomSheet(
    private val bottomSheetCallback : BottomSheetBehavior.BottomSheetCallback
) : BottomSheetDialogFragment() {

    private val viewModel: MeetUpMapViewModel by viewModels()

    private var _binding: ModalBottomSheetsMeetingListBinding? = null
    private val binding: ModalBottomSheetsMeetingListBinding
        get() = _binding!!

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

                /** Behavior 상태
                 *  STATE_EXPANDED : 완전히 펼쳐진 상태
                 *  STATE_COLLAPSED : 접혀있는 상태
                 *  STATE_HIDDEN : 아래로 숨겨진 상태 (보이지 않음)
                 *  STATE_HALF_EXPANDED : 절반으로 펼쳐진 상태
                 *  STATE_DRAGGING : 드래깅되고 있는 상태
                 *  STATE_SETTLING : 드래그/스와이프 직후 고정된 상태
                 */
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                behavior.isHideable = false // 아래로 드래그 시 숨길지 여부를 결정함
                behavior.halfExpandedRatio = 0.8f // 최대 높이 비율 지정(0 ~ 1.0)
                behavior.peekHeight = 200 // 최소 높이 지정

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