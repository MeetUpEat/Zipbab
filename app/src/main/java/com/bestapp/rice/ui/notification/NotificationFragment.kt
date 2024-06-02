package com.bestapp.rice.ui.notification

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bestapp.rice.databinding.FragmentNotificationBinding
import com.bestapp.rice.ui.BaseFragment


class NotificationFragment : BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::inflate) {
    private lateinit var muTiAdapter: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private var itemList = ArrayList<NotificationType>()

    private fun initViews() {
        /*itemList.add(NotificationType.MainNotification(dec = "공지안내드립니다.", uploadDate = "6시간전"))
        itemList.add(
            NotificationType.UserNotification(
                dec = "...가 모임에 참가 하였 습니다.",
                uploadDate = "30초전"
            )
        ) //firestore에서 값을 받아올 부분 추후에 viewModel에서 가져올예정*/
        muTiAdapter = NotificationAdapter(itemList)

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = itemList.get(viewHolder.adapterPosition)
                val deletedIndex = viewHolder.adapterPosition

                if(direction == ItemTouchHelper.LEFT) {
                    muTiAdapter.removeItem(deletedIndex)
                } else if(direction == ItemTouchHelper.RIGHT) {
                    muTiAdapter.removeItem(deletedIndex)
                }
            }
        }).attachToRecyclerView(binding.recyclerview)

        binding.recyclerview.adapter = muTiAdapter

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}