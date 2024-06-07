package com.bestapp.rice.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.bestapp.rice.model.toProfileEditUi
import com.bestapp.rice.ui.profile.util.PostLinearSnapHelper
import com.bestapp.rice.ui.profile.util.SnapOnScrollListener
import com.bestapp.rice.util.loadOrDefault
import com.bestapp.rice.util.setVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    private val galleryAdapter = ProfileGalleryAdapter {
        showPostImage(it)
    }

    private val postAdapter = PostAdapter()
    private val postLinearSnapHelper = PostLinearSnapHelper()

    private val viewModel: ProfileViewModel by viewModels()

    private var onBackPressedCallback: OnBackPressedCallback? = null

    private val args: ProfileFragmentArgs by navArgs()

    private var countOfPostImage = 0

    private fun showPostImage(postUiState: PostUiState) {
        countOfPostImage = postUiState.images.size
        changePostVisibility(true)
        changePostOrder(0)
        postAdapter.submitList(postUiState.images)
    }

    private fun changePostVisibility(isVisible: Boolean) {
        binding.vModalBackground.setVisibility(isVisible)
        binding.rvPost.setVisibility(isVisible)
        binding.tvPostOrder.setVisibility(isVisible)

        // 사진 게시물 View를 끌 때, 이전에 봤던 포지션을 초기화 하지 않으면 게시물을 다시 눌렀을 때, 이전 포지션부터 보인다.
        if (isVisible.not()) {
            binding.rvPost.scrollToPosition(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadUserInfo(args.userDocumentId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedDispatcher()
        setRecyclerView()
        setListener()
        setObserve()
    }

    private fun setBackPressedDispatcher() {
        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (isEnabled.not()) {
                    return@addCallback
                }

                if (binding.vModalBackground.isVisible) {
                    changePostVisibility(false)
                } else if (binding.vModalBackgroundForLargeProfile.isVisible) {
                    viewModel.closeLargeProfile()
                } else {
                    if (!findNavController().popBackStack()) {
                        requireActivity().finish()
                    }
                }
            }
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
            viewModel.closeLargeProfile()
        }
        binding.mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
    }

    private fun changeProfileLargeImageVisibility(isVisible: Boolean, imageUrl: String? = null) {
        if (imageUrl != null) {
            binding.ivProfileLargeImage.loadOrDefault(imageUrl)
        }
        binding.ivProfileLargeImage.setVisibility(isVisible)
        binding.vModalBackgroundForLargeProfile.setVisibility(isVisible)
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileUiState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    setListenerAboutSelfProfile(state)
                    setUI(state)
                    setSelfProfileVisibility(state.isSelfProfile)
                    changeProfileLargeImageVisibility(state.isProfileClicked, state.profileImage)
                }
        }
    }

    private fun setListenerAboutSelfProfile(profileUiState: ProfileUiState) {
        binding.btnEditProfile.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment(profileUiState.toProfileEditUi())
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

    private fun setUI(profileUiState: ProfileUiState) {
        setUserProfileInfo(profileUiState)
        galleryAdapter.submitList(profileUiState.postUiStates)
    }

    private fun setUserProfileInfo(profileUiState: ProfileUiState) {
        // 닉네임 & 식별자
        binding.tvNickname.text = profileUiState.nickname
        binding.tvDistinguishNum.text =
            getString(R.string.profile_distinguish_format_8).format(profileUiState.userDocumentID)

        // 프로필 이미지
        binding.ivProfileImage.load(profileUiState.profileImage) {
            placeholder(R.drawable.sample_profile_image)
        }

        // 모임 횟수
        val badge = MeetingBadge.from(profileUiState.meetingCount)
        binding.ivMeetBadge.setImageResource(badge.drawableRes)
        binding.tvMeetCount.text = profileUiState.meetingCount.toString()

        // 매너 온도
        binding.lpiTemperature.progress = profileUiState.temperature.toInt()
        val temperature = UserTemperature.from(profileUiState.temperature)
        val color = resources.getColor(temperature.colorRes, requireActivity().theme)
        binding.lpiTemperature.setIndicatorColor(color)

        binding.tvTemperature.text =
            getString(R.string.temperature_format).format(profileUiState.temperature)
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

    override fun onDestroyView() {
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val CORRECTION_NUM_FOR_STARTING_ONE = 1
    }
}