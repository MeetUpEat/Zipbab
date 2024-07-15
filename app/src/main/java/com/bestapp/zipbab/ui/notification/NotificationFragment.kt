package com.bestapp.zipbab.ui.notification

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding: FragmentNotificationBinding
        get() = _binding!!

    private lateinit var muTiAdapter: NotificationAdapter
    private val notifyViewModel: NotificationViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            binding.recyclerview.isVisible = true

            sendNotification()
        } else {
            binding.recyclerview.isVisible = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessCheck()
        initViews()
        setObserve()
    }

    var itemList = arrayListOf<NotificationTypeResponse>()
    var itemTrans = arrayListOf<NotificationTypeResponse>()

    private fun initViews() {

        muTiAdapter = NotificationAdapter()
        itemSwipe()

        notifyViewModel.getUserData.observe(viewLifecycleOwner) {
            if(it.notifications.isEmpty()) {
                return@observe
            }

            itemList = it.notifications as ArrayList<NotificationTypeResponse>
            itemTrans = it.notifications as ArrayList<NotificationTypeResponse>

            muTiAdapter.submitList(itemList)
            binding.recyclerview.adapter = muTiAdapter
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun accessCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //33버전 이상
            when {
                (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) -> {
                    sendNotification()
                }
                (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("권한 설정")
                        .setMessage("알림설정을 켜시려면 동의 버튼을 눌러주세요")
                        .setPositiveButton("동의",
                            DialogInterface.OnClickListener { _, _ ->
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            })
                        .setNegativeButton("거부",
                            DialogInterface.OnClickListener { _, _ ->
                                Toast.makeText(context, "권한설정을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                            })
                        .show()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            sendNotification() //33버전 미만
        }
    }

    private fun sendNotification() {
        binding.recyclerview.isVisible = true

        notifyViewModel.getUserData() //list불러오는 로직
//        notifyViewModel.getAccessToken() //알림 보내는 로직
//
//        val notificationData = NotificationData(
//            title = "모임신청알림",
//            body = "...이 모임에 신청 하였습니다."
//        )
//
//        getToken { token ->
//
//            val message = Message(
//                token = token,
//                notification = notificationData
//            )
//
//            notifyViewModel.accesskey.observe(viewLifecycleOwner) {
//                val resultToken : String = "Bearer " + it
//
//                notifyViewModel.sendMsgKaKao(PushNotification(message = message), resultToken)
//            }
//        }
    }

    private var itemTouchHelper: ItemTouchHelper? = null
    private var deletedIndex: Int = -1
    private val itemTouchCallback = object : ItemTouchHelper.Callback() {
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
            deletedIndex = viewHolder.bindingAdapterPosition

            if (direction == ItemTouchHelper.LEFT) {
                muTiAdapter.removeItem(itemList, deletedIndex)
                notifyViewModel.removeNotifyList(deletedIndex) //삭제로직
            } else if (direction == ItemTouchHelper.RIGHT) {
                //muTiAdapter.removeItem(itemList, deletedIndex)
                AlertDialog.Builder(requireContext())
                    .setTitle("알림")
                    .setMessage("모임 신청을 승인하시려면 수락 버튼을 눌러주세요!!")
                    .setPositiveButton("수락",
                        DialogInterface.OnClickListener { _, _ ->
                            notifyViewModel.approveMember(itemTrans[deletedIndex].meetingDocumentId, itemTrans[deletedIndex].userDocumentId) //모임 신청에서 넘겨주는 값
                        })
                    .setOnDismissListener {
                        viewHolder.itemView
                            .animate()
                            .translationX(0f)
                            .withEndAction {
                                itemTouchHelper?.attachToRecyclerView(null)
                                itemTouchHelper?.attachToRecyclerView(binding.recyclerview)
                            }.start()
                    }
                    .setNegativeButton("반려",
                        DialogInterface.OnClickListener { _, _ ->
                            Toast.makeText(context, "모임 신청을 거부 하였습니다.", Toast.LENGTH_SHORT).show()
                            muTiAdapter.removeItem(itemList, deletedIndex)
                            notifyViewModel.removeNotifyList(deletedIndex)
                        })
                    .show()
            }
        }
    }

    private fun setObserve() {
        notifyViewModel.approveUser.observe(viewLifecycleOwner) {
            if(it) {
                Toast.makeText(requireContext(), "모임신청을 수락하였습니다.", Toast.LENGTH_SHORT).show()
                //notifyViewModel.transUserMeeting(itemTrans[deletedIndex].meetingDocumentId, itemTrans[deletedIndex].userDocumentId)
                muTiAdapter.removeItem(itemList, deletedIndex)
                notifyViewModel.removeNotifyList(deletedIndex)
            } else {
                Toast.makeText(requireContext(), "알 수 없는 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                itemTouchHelper?.attachToRecyclerView(null)
                itemTouchHelper?.attachToRecyclerView(binding.recyclerview)
//                    viewHolder.itemView
//                        .animate()
//                        .translationX(0f)
//                        .withEndAction {
//                            itemTouchHelper?.attachToRecyclerView(null)
//                            itemTouchHelper?.attachToRecyclerView(binding.recyclerview)
//                        }.start()
            }
        }
    }
    private fun itemSwipe() {
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper?.attachToRecyclerView(binding.recyclerview)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}