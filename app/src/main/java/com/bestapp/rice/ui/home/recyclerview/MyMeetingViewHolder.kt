package com.bestapp.rice.ui.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemMyMeetBinding
import com.bestapp.rice.model.MeetingUiState

class MyMeetingViewHolder(
    parent: ViewGroup,
    private val onMyMeetingClick: (meetingUiState: MeetingUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_my_meet, parent, false)
) {

    private val binding = ItemMyMeetBinding.bind(itemView)

    // TODO - 17. with(binding)을 이용해서 반복되는 binding을 빼면 좋겠습니다.
    //  Q. 마지막 값이 반환되는데 괜찮은 건가요?
    //  A. bind 함수를 호출한 후 반환 값을 받지는 않기 때문에 괜찮을 것 같아요.+
    fun bind(
        meetingUiState: MeetingUiState
    ) {
        binding.iv.load(meetingUiState.titleImage)
        binding.tvTitle.text = meetingUiState.title
        binding.tvLocation.text = meetingUiState.placeLocationUiState.locationAddress
        binding.root.setOnClickListener {
            onMyMeetingClick(meetingUiState)
        }
    }
}
