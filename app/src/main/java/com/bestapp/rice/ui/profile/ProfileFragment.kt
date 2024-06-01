package com.bestapp.rice.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentProfileBinding
import com.bestapp.rice.model.ImageUiState
import com.bestapp.rice.model.MeetingBadge
import com.bestapp.rice.model.UserTemperature
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.profile.util.PostLinearSnapHelper
import com.bestapp.rice.ui.profile.util.SnapOnScrollListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val galleryAdapter = ProfileGalleryAdapter {
        showPostImage(it.imageUiStates)
    }

    private val postAdapter = PostAdapter()
    private val postLinearSnapHelper = PostLinearSnapHelper()

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory()
    }

    private val args: ProfileFragmentArgs by navArgs()

    private var countOfPostImage = 0

    private fun showPostImage(imageUiStates: List<ImageUiState>) {
        countOfPostImage = imageUiStates.size
        changePostVisibility(true)
        changePostOrder(0)
        postAdapter.submitList(imageUiStates)
    }

    private fun changePostVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE

        binding.vModalBackground.visibility = visibility
        binding.rvPost.visibility = visibility
        binding.tvPostOrder.visibility = visibility
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel.loadUserInfo(args.userDocumentId)
        viewModel.loadUserInfo("1q2W3e4R1q2W3e4R1q2W3e4R")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        setListener()
        setObserve()
    }

    private fun setRecyclerView() {
        binding.rvGalleryItem.adapter = galleryAdapter
        binding.rvPost.adapter = postAdapter
        postLinearSnapHelper.attachToRecyclerView(binding.rvPost)
        val snapOnScrollListener = SnapOnScrollListener(postLinearSnapHelper) {
            changePostOrder(it)
        }
        binding.rvPost.addOnScrollListener(snapOnScrollListener)
    }

    private fun changePostOrder(order: Int) {
        binding.tvPostOrder.text = getString(R.string.post_order_format).format(
            order + CORRECTION_NUM_FOR_STARTING_ONE,
            countOfPostImage
        )
    }

    private fun setListener() {
        binding.vModalBackground.setOnClickListener {
            changePostVisibility(false)
        }
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.userUiState.flowWithLifecycle(lifecycle)
                    .collectLatest { userUiState ->
                        setUI(userUiState)
                    }
            }
        }
    }

    private fun setUI(userUiState: UserUiState) {
        setTemperatureUI(userUiState)
        galleryAdapter.submitList(userUiState.postUiStates)
    }

    private fun setTemperatureUI(userUiState: UserUiState) {
        // 닉네임 & 식별자
        binding.tvNickname.text = userUiState.nickName
        binding.tvDistinguishNum.text =
            getString(R.string.profile_distinguish_format_8).format(userUiState.userDocumentID)

        // 프로필 이미지
        binding.ivProfileImage.load(userUiState.profileImage) {
            placeholder(R.drawable.sample_profile_image)
        }

        // 모임 횟수
        val badge = MeetingBadge.from(userUiState.meetingCount)
        binding.ivMeetBadge.setImageResource(badge.drawableRes)
        binding.tvMeetCount.text = userUiState.meetingCount.toString()

        // 매너 온도
        binding.lpiTemperature.progress = userUiState.temperature.toInt()
        val temperature = UserTemperature.from(userUiState.temperature)
        val color = resources.getColor(temperature.colorRes, requireActivity().theme)
        binding.lpiTemperature.setIndicatorColor(color)

        binding.tvTemperature.text =
            getString(R.string.temperature_format).format(userUiState.temperature)
        binding.tvTemperature.setTextColor(color)
    }

    companion object {
        private const val CORRECTION_NUM_FOR_STARTING_ONE = 1
    }
}