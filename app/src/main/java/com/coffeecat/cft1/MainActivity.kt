package com.coffeecat.cft1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    // 請求權限的 Launcher
    @RequiresApi(Build.VERSION_CODES.O)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // 拒絕權限時，直接跳轉系統通知設定頁面，讓使用者手動開啟
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences("task_list_prefs", MODE_PRIVATE)

        // 建立通知頻道
        createNotificationChannel()

        // 請求通知權限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 讀取儲存的待辦清單
        val savedTasks = sharedPreferences.getString("task_list", "") ?: ""

        setContent {
            TaskListScreen(savedTasks) { updatedTasks ->
                // 儲存更新後的清單
                sharedPreferences.edit {
                    putString("task_list", updatedTasks)
                }

                if (updatedTasks.isNotEmpty()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        showNotification(updatedTasks)
                    }
                } else {
                    cancelNotification()
                }
            }
        }
    }

    @Composable
    fun TaskListScreen(savedTasks: String, onTasksUpdated: (String) -> Unit) {
        var task by remember { mutableStateOf("") }
        var tasks by remember { mutableStateOf(savedTasks.split("\n").filter { it.isNotEmpty() }) }
        val context = LocalContext.current
        val notificationManager = NotificationManagerCompat.from(context)

        LaunchedEffect(tasks) {
            if (tasks.isEmpty()) {
                notificationManager.cancel(1)
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    showNotification(tasks.joinToString("\n"))
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = task,
                onValueChange = { task = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text("輸入待辦事項") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (task.isNotEmpty()) {
                        tasks = tasks + task
                        onTasksUpdated(tasks.joinToString("\n"))
                        task = ""
                    }
                })
            )

            Button(onClick = {
                if (task.isNotEmpty()) {
                    tasks = tasks + task
                    onTasksUpdated(tasks.joinToString("\n"))
                    task = ""
                }
            }) {
                Text("新增")
            }

            Spacer(modifier = Modifier.height(16.dp))

            tasks.forEachIndexed { index, taskItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• $taskItem",
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        tasks = tasks.filterIndexed { i, _ -> i != index }
                        onTasksUpdated(tasks.joinToString("\n"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(taskList: String) {
        val deleteIntent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("task_list", taskList)
        }
        val deletePendingIntent = android.app.PendingIntent.getBroadcast(
            this,
            0,
            deleteIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, "task_notification_channel")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("待辦清單")
            .setContentText("點擊查看詳細內容")
            .setStyle(NotificationCompat.BigTextStyle().bigText(taskList))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setDeleteIntent(deletePendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun cancelNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(1)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_notification_channel",
                "Task Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
