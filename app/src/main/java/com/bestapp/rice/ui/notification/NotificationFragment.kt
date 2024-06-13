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
import com.bestapp.rice.data.notification.ForFcm
import com.bestapp.rice.data.notification.NotificationKey
import com.bestapp.rice.data.notification.PushMsgJson
import com.bestapp.rice.data.notification.SendNotificationRequest
import com.bestapp.rice.databinding.FragmentNotificationBinding
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            binding.recyclerview.isVisible = true
            getToken { token, deviceId ->
                notifyViewModel.registerTokenKaKao("17110993", deviceId, token) // 테스트용 코드
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

        muTiAdapter = NotificationAdapter()
        itemSwipe()
        /*muTiAdapter.submitList(itemList)
        binding.recyclerview.adapter = muTiAdapter*/

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun accessCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
            //33버전 미만에서는 알림권한이 따로 필요없었음
        }
    }

    private fun sendNotification() {

        getToken { token, deviceId ->
            notifyViewModel.registerTokenKaKao("17110993", deviceId, token) // 테스트용 코드
        }

        val notificationKey = NotificationKey(
            title = "알림",
            body = "...이 모임에 참여했습니다.",
            tag = "user"
        )

        val forFcm = ForFcm(
            collapse = "user",
            timeToLive = 17200,
            priority = "normal",
            notification = notificationKey
        )

        val pushMsg = PushMsgJson(
            forFcm = forFcm
        )

        val uuid = mutableListOf<String>()
        uuid.add("17110993")

        notifyViewModel.sendMsgKaKao(SendNotificationRequest(uuids = uuid, pushMessage = pushMsg, bypass = false))
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

    private fun getToken(callback: (String, String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                FirebaseInstallations.getInstance().id.addOnCompleteListener { idTask ->
                    if (idTask.isSuccessful) {
                        callback(token, idTask.result.toString())
                    } else {
                        Log.e("Installations", "Unable to get Installation ID")
                    }
                }
            } else {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}