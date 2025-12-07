package com.coffeecat.cft1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 從 Intent 取出原本的待辦清單內容
        val taskList = intent.getStringExtra("task_list") ?: return

        // 重新發送通知
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            showResurrectedNotification(context, taskList)
        }
    }

    private fun showResurrectedNotification(context: Context, taskList: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        // 為了避免迴圈，這裡也要設定 DeleteIntent
        val deleteIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("task_list", taskList)
        }
        val deletePendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            0,
            deleteIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "task_notification_channel")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("待辦清單")
            .setContentText("點擊查看詳細內容")
            .setStyle(NotificationCompat.BigTextStyle().bigText(taskList))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setDeleteIntent(deletePendingIntent) // 設定刪除時觸發
            .build()

        notificationManager.notify(1, notification)
    }
}