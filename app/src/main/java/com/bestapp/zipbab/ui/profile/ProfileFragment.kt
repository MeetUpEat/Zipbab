package com.bestapp.zipbab.ui.profile

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bestapp.zipbab.R
import com.bestapp.zipbab.args.ImagePostSubmitArgs
import com.bestapp.zipbab.databinding.FragmentProfileBinding
import com.bestapp.zipbab.model.MeetingBadge
import com.bestapp.zipbab.model.PostUiState
import com.bestapp.zipbab.model.UploadState
import com.bestapp.zipbab.model.UserTemperature
import com.bestapp.zipbab.model.toProfileEditArgs
import com.bestapp.zipbab.ui.profile.util.PostLinearSnapHelper
import com.bestapp.zipbab.ui.profile.util.SnapOnScrollListener
import com.bestapp.zipbab.ui.profilepostimageselect.ProfilePostImageSelectFragment
import com.bestapp.zipbab.util.loadOrDefault
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    private val galleryAdapter = ProfileGalleryAdapter(
        onClick = {
            showPostImage(it)
            viewModel.onPostClick(it)
        },
        onLongClick = {
            viewModel.onPostLongClick(it)
        }
    )

    private val deletePostDialog by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_post_dialog_title))
            .setMessage(getString(R.string.delete_post_dialog_message))
            .setNeutralButton(getString(R.string.delete_post_dialog_neutral)) { _, _ ->
                viewModel.resetDeleteState()
            }
            .setPositiveButton(getString(R.string.delete_post_dialog_positive)) { _, _ ->
                viewModel.deletePost()
            }
            .setOnDismissListener {
                viewModel.resetDeleteState()
            }
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
        binding.vModalBackground.isVisible = isVisible
        binding.rvPost.isVisible = isVisible
        binding.tvPostOrder.isVisible = isVisible
        // 숨김 처리만 여기서 하고, 보이는 처리는 본인 프로필 여부가 필요하기 때문에 setObserve에서 처리
        if (isVisible.not()) {
            binding.btnReportPost.isVisible = false
            binding.btnDeletePost.isVisible = false
        }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewAttribute()
        setBackPressedDispatcher()
        setRecyclerView()
        setListener()
        setObserve()
    }

    private fun setViewAttribute() {
        binding.ivProfileImage.clipToOutline = true
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
        binding.rvGalleryItem.addItemDecoration(object : ItemDecoration() {
            private val marginSize =
                binding.root.context.resources.getDimension(R.dimen.default_margin8).toInt()

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                val position = parent.getChildAdapterPosition(view)
                when (Location.get(position)) {
                    Location.START -> {
                        outRect.left = 0
                        outRect.right = marginSize
                    }

                    Location.MIDDLE -> {
                        outRect.left = marginSize / 2
                        outRect.right = marginSize / 2
                    }

                    Location.END -> {
                        outRect.left = marginSize
                        outRect.right = 0
                    }

                    null -> {
                        outRect.left = 0
                        outRect.right = 0
                    }
                }
            }
        })
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

    private fun setListener() = with(binding) {
        vModalBackground.setOnClickListener {
            viewModel.resetReportState()
            changePostVisibility(false)
        }
        tvHeaderForTemperature.setOnClickListener {
            temperatureInstructionView.root.visibility = View.VISIBLE
        }
        ivProfileImage.setOnClickListener {
            viewModel.onProfileImageClicked()
        }
        vModalBackgroundForLargeProfile.setOnClickListener {
            viewModel.resetReportState()
            viewModel.closeLargeProfile()
        }
        mt.setNavigationOnClickListener {
            if (!findNavController().popBackStack()) {
                requireActivity().finish()
            }
        }
        btnReportPost.setOnClickListener {
            viewModel.reportPost()
        }
        btnDeletePost.setOnClickListener {
            viewModel.onDeletePost()
        }
        btnReportUser.setOnClickListener {
            viewModel.reportUser()
        }
    }

    private fun changeProfileLargeImageVisibility(isVisible: Boolean, imageUrl: String? = null) {
        if (imageUrl != null) {
            binding.ivProfileLargeImage.loadOrDefault(imageUrl)
        }
        binding.ivProfileLargeImage.isVisible = isVisible
        binding.vModalBackgroundForLargeProfile.isVisible = isVisible
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileUiState.flowWithLifecycle(lifecycle)
                .collect { state ->
                    setListenerAboutSelfProfile(state)
                    setUI(state)
                    setSelfProfileVisibility(state.isSelfProfile)
                    changeProfileLargeImageVisibility(state.isProfileClicked, state.profileImage)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reportState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    when (state) {
                        ReportState.Complete -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.report_done), Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetReportState()
                        }

                        ReportState.Default -> Unit
                        ReportState.Fail -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.report_fail),
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetReportState()
                        }

                        is ReportState.PendingPost -> {
                            binding.btnReportPost.isVisible = state.isSelfProfile.not()
                            binding.btnDeletePost.isVisible = state.isSelfProfile
                        }

                        is ReportState.ProgressPost -> Unit
                        is ReportState.ProgressProfile -> Unit
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteState.flowWithLifecycle(lifecycle)
                .collect { state ->
                    when (state) {
                        DeleteState.Pending -> deletePostDialog.show()
                        DeleteState.Complete -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.delete_post_done), Toast.LENGTH_SHORT
                            ).show()
                            changePostVisibility(false)
                            viewModel.resetDeleteState()
                        }

                        DeleteState.Fail -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.delete_post_fail), Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetDeleteState()
                        }

                        DeleteState.Progress -> Unit
                        DeleteState.Default -> Unit
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uploadState.flowWithLifecycle(lifecycle)
                .collect { state ->
                    when (state) {
                        is UploadState.Default -> Unit
                        is UploadState.Fail -> Toast.makeText(
                            requireContext(),
                            "업로드에 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()

                        is UploadState.InProgress -> Unit
                        is UploadState.Pending -> Unit
                        is UploadState.ProcessPost -> Unit
                        is UploadState.SuccessPost -> Unit
                    }
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<ImagePostSubmitArgs>(
                ProfilePostImageSelectFragment.POST_IMAGE_SELECT_KEY
            ).observe(viewLifecycleOwner) {
                remove<ImagePostSubmitArgs>(ProfilePostImageSelectFragment.POST_IMAGE_SELECT_KEY)
                viewModel.submitPost(it)
            }
        }
    }

    private fun setListenerAboutSelfProfile(profileUiState: ProfileUiState) {
        binding.btnEditProfile.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment(profileUiState.toProfileEditArgs())
            findNavController().navigate(action)
        }
        binding.btnAddImage.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToProfilePostImageSelectFragment(args.userDocumentID)
            findNavController().navigate(action)
        }
    }

    private fun setSelfProfileVisibility(isSelfProfile: Boolean) {
        binding.btnEditProfile.isVisible = isSelfProfile
        binding.btnAddImage.isVisible = isSelfProfile
    }

    private fun setUI(profileUiState: ProfileUiState) {
        setUserProfileInfo(profileUiState)
        galleryAdapter.submitList(profileUiState.postUiStates)
    }

    private fun setUserProfileInfo(profileUiState: ProfileUiState) = with(binding) {
        // 신고 버튼
        btnReportUser.isGone =
            profileUiState.userDocumentID.isBlank() || profileUiState.isSelfProfile
        btnReportPost.isEnabled = profileUiState.isSelfProfile.not()

        // 닉네임 & 식별자
        tvNickname.text = profileUiState.nickname
        tvDistinguishNum.text =
            getString(R.string.profile_distinguish_format_8).format(profileUiState.userDocumentID)

        // 프로필 이미지
        ivProfileImage.loadOrDefault(profileUiState.profileImage)

        // 모임 횟수
        val badge = MeetingBadge.from(profileUiState.meetingCount)
        ivMeetBadge.setImageResource(badge.drawableRes)
        tvMeetCount.text = profileUiState.meetingCount.toString()

        // 매너 온도
        lpiTemperature.progress = profileUiState.temperature.toInt()
        val temperature = UserTemperature.from(profileUiState.temperature)
        val color = resources.getColor(temperature.colorRes, requireActivity().theme)
        lpiTemperature.setIndicatorColor(color)

        tvTemperature.text =
            getString(R.string.temperature_format).format(profileUiState.temperature)
        tvTemperature.setTextColor(color)
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

    override fun onResume() {
        super.onResume()

        viewModel.loadUserInfo(args.userDocumentID)
    }

    override fun onDestroyView() {
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        _binding = null
        viewModel.resetReportState()
        viewModel.resetDeleteState()

        super.onDestroyView()
    }

    companion object {
        private const val CORRECTION_NUM_FOR_STARTING_ONE = 1
    }
}