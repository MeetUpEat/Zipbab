package com.bestapp.rice.ui.profilepostimageselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentProfilePostImageSelectBinding
import com.bestapp.rice.model.toArg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePostImageSelectFragment : Fragment() {

    private var _binding: FragmentProfilePostImageSelectBinding? = null
    private val binding: FragmentProfilePostImageSelectBinding
        get() = _binding!!

    private val selectedImageAdapter = SelectedImageAdapter()
    private val viewModel: PostImageSelectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePostImageSelectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setListener()
    }

    private fun setListener() {
        binding.vPermissionRequestBackground.setOnClickListener {
            if (parentFragmentManager.fragments.last() !is ProfilePostImageSelectFragment) {
                return@setOnClickListener
            }
            val action =
                ProfilePostImageSelectFragmentDirections.actionProfilePostImageSelectFragmentToImagePermissionModalBottomSheet()
            findNavController().navigate(action)
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
        binding.mt.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.done -> {
                    // 아이템이 한 개 이상 클릭된 경우에만 완료 버튼을 누를 수 있어야 한다.
                    if (selectedImageAdapter.itemCount < MIN_SELECTED_ITEM) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.post_image_select_min_select_item),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnMenuItemClickListener true
                    }
                    // TODO : 아래 함수는 이미지 편집 화면이 완료된 이후에 호출하도록 수정
//                    navigateToEdit()
                    viewModel.submit()
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToEdit() {
        val selectedImage = selectedImageAdapter.getItem().map {
            it.toArg()
        }.toTypedArray()
        val action =
            ProfilePostImageSelectFragmentDirections.actionProfilePostImageSelectFragmentToProfilePostImageEditFragment(
                selectedImage
            )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val MIN_SELECTED_ITEM = 1
    }
}