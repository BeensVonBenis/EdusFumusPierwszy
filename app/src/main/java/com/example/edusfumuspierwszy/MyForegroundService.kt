package com.example.edusfumuspierwszy

import java.util.concurrent.TimeUnit
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.work.*
import com.google.gson.Gson

class MyForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(1, notification)
        // Schedule the background task to update the school plan data
        scheduleSchoolPlanWorker()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val channelId = "school_plan_service"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "School Plan Update",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }

        return Notification.Builder(this, channelId)
            .setContentTitle("Updating School Plan")
            .setContentText("School plan is being updated in the background.")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }

    private fun scheduleSchoolPlanWorker() {
        val workRequest = PeriodicWorkRequestBuilder<SchoolPlanWorker>(10, TimeUnit.SECONDS).build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
