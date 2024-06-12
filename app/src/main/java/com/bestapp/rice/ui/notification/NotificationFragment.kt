package com.bestapp.rice.ui.notification

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.bestapp.rice.data.notification.NotificationKey
import com.bestapp.rice.data.notification.PushMsgJson
import com.bestapp.rice.databinding.FragmentNotificationBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding: FragmentNotificationBinding
        get() = _binding!!

    private lateinit var muTiAdapter: NotificationAdapter
    private val notifyViewModel: NotificationViewModel by viewModels()

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isChecked: Boolean ->
        if (isChecked) {

            binding.recyclerview.isVisible = true
            //FireBaseMessageReceiver()
            val result = getToken()
            notifyViewModel.getUserUUID()

            val notificationKey = NotificationKey(
                title = "알림",
                body = "...이 모임에 참여했습니다.",
                icon = "",
                sound = "default",
                tag = "user",
                color = ""
            )

            val pushMsg = PushMsgJson(
                collapse = "user",
                timeToLive = 17200,
                priority = "normal",
                notification = notificationKey
            )

            notifyViewModel.userInfo.observe(viewLifecycleOwner) {
                notifyViewModel.registerTokenKaKao(it.toString(), result.second, result.first)
                notifyViewModel.sendMsgKaKao(listOf(it), pushMsg)
            }

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

        initViews()
        accessCheck()
    }

    private var itemList = ArrayList<NotificationType>()

    private fun initViews() {
        //notifyViewModel.notifyKaKao()

        /*itemList.add(NotificationType.MainNotification(dec = "공지안내드립니다.", uploadDate = "6시간전"))
        itemList.add(
            NotificationType.UserNotification(
                dec = "...가 모임에 참가 하였 습니다.",
                uploadDate = "30초전"
            )
        ) //firestore에서 값을 받아올 부분 추후에 viewModel에서 가져올예정*/

        muTiAdapter = NotificationAdapter()
        itemSwipe()
        muTiAdapter.submitList(itemList)
        binding.recyclerview.adapter = muTiAdapter

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun accessCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                //FireBaseMessageReceiver()
                getToken()

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
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
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            //33버전 미만에서는 알림권한이 따로 필요없었음
        }
    }

    private fun itemSwipe() {
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

                if (direction == ItemTouchHelper.LEFT) {
                    muTiAdapter.removeItem(itemList, deletedIndex)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    muTiAdapter.removeItem(itemList, deletedIndex)
                }
            }
        }).attachToRecyclerView(binding.recyclerview)
    }

    private fun getToken() : Pair<String, String> {
        var tokenInfo : String = ""
        var deviceId: String = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token.toString()
            tokenInfo = msg.split(":").get(1)
            Log.d("FCM", tokenInfo)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        })

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Log.d("Installations", "Installation ID: " + task.result)
                deviceId = task.result.toString()
            } else {
                Log.e("Installations", "Unable to get Installation ID")
            }
        }
        return Pair(tokenInfo, deviceId)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}