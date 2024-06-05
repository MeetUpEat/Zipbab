package com.bestapp.rice.ui.profile

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentProfileBinding
import com.bestapp.rice.model.MeetingBadge
import com.bestapp.rice.model.PostUiState
import com.bestapp.rice.model.UserTemperature
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.ui.BaseFragment
import com.bestapp.rice.ui.profile.util.PostLinearSnapHelper
import com.bestapp.rice.ui.profile.util.SnapOnScrollListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val galleryAdapter = ProfileGalleryAdapter {
        showPostImage(it)
    }

    private val postAdapter = PostAdapter()
    private val postLinearSnapHelper = PostLinearSnapHelper()

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory()
    }

    private val args: ProfileFragmentArgs by navArgs()

    private var countOfPostImage = 0

    private fun showPostImage(postUiState: PostUiState) {
        countOfPostImage = postUiState.images.size
        changePostVisibility(true)
        changePostOrder(0)
        postAdapter.submitList(postUiState.images)
    }

    private fun changePostVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE

        binding.vModalBackground.visibility = visibility
        binding.rvPost.visibility = visibility
        binding.tvPostOrder.visibility = visibility

        // 사진 게시물 View를 끌 때, 이전에 봤던 포지션을 초기화 하지 않으면 게시물을 다시 눌렀을 때, 이전 포지션부터 보인다.
        if (isVisible.not()) {
            binding.rvPost.scrollToPosition(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isEnabled.not()) {
                return@addCallback
            }

            if (binding.vModalBackground.isVisible) {
                changePostVisibility(false)
            } else if (binding.vModalBackgroundForLargeProfile.isVisible) {
                changeProfileLargeImageVisibility(false, null)
            } else {
                if (!findNavController().popBackStack()) {
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadUserInfo(args.userDocumentId)
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
        binding.rvPost.itemAnimator = null // adapter item 갯수가 바뀔 때, position에 따른 애니메이션 효과 삭제

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
        binding.tvHeaderForTemperature.setOnClickListener {
            binding.temperatureInstructionView.root.visibility = View.VISIBLE
        }
        binding.ivProfileImage.setOnClickListener {
            viewModel.onProfileImageClicked()
        }
        binding.vModalBackgroundForLargeProfile.setOnClickListener {
            changeProfileLargeImageVisibility(false, null)
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    private fun changeProfileLargeImageVisibility(isVisible: Boolean, imageUrl: String?) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.ivProfileLargeImage.load(imageUrl)
        binding.ivProfileLargeImage.visibility = visibility
        binding.vModalBackgroundForLargeProfile.visibility = visibility
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.userUiState.flowWithLifecycle(lifecycle)
                    .collectLatest { userUiState ->
                        setListenerAboutSelfProfile(userUiState)
                        setUI(userUiState)
                    }
            }

            launch {
                viewModel.isSelfProfile.flowWithLifecycle(lifecycle)
                    .collectLatest { isSelfProfile ->
                        setSelfProfileVisibility(isSelfProfile)
                    }
            }
            launch {
                viewModel.profileUiState.flowWithLifecycle(lifecycle)
                    .collectLatest { imageUiState ->
                        changeProfileLargeImageVisibility(true, imageUiState)
                    }
            }
        }
    }

    private fun setListenerAboutSelfProfile(userUiState: UserUiState) {
        binding.btnEditProfile.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment(userUiState)
            findNavController().navigate(action)
        }
        binding.btnAddImage.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToProfilePostImageSelectFragment()
            findNavController().navigate(action)
        }
    }

    private fun setSelfProfileVisibility(isSelfProfile: Boolean) {
        val visibility = if (isSelfProfile) View.VISIBLE else View.GONE
        binding.btnEditProfile.visibility = visibility
        binding.btnAddImage.visibility = visibility
    }

    private fun setUI(userUiState: UserUiState) {
        setUserProfileInfo(userUiState)
        galleryAdapter.submitList(userUiState.postUiStates)
    }

    private fun setUserProfileInfo(userUiState: UserUiState) {
        // 닉네임 & 식별자
        binding.tvNickname.text = userUiState.nickname
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

    fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val instructionView = binding.temperatureInstructionView.root
        if (instructionView.isVisible && isWithOutViewBounds(instructionView, event)) {
            instructionView.visibility = View.GONE
            return true
        }
        return false
    }

    private fun isWithOutViewBounds(view: View, event: MotionEvent): Boolean {
        val xy = IntArray(2)
        view.getLocationOnScreen(xy)
        val (x, y) = xy

        return event.x < x || event.x > x + view.width || event.y < y || event.y > y + view.height
    }

    companion object {
        private const val CORRECTION_NUM_FOR_STARTING_ONE = 1
    }
}