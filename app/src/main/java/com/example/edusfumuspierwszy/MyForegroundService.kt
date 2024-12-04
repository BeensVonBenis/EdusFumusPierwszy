package com.example.edusfumuspierwszy

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.LocalDateTime

class MyForegroundService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val notificationInterval = 10 * 10000000L // 10 seconds
    private val notificationId = 1 // Consistent notification ID for overwriting

    override fun onCreate() {
        super.onCreate()

        // Create and start the service in the foreground with an initial notification
        val notification = createNotification("Initial Notification", "Service started")
        startForeground(notificationId, notification)

        // Schedule repeated notifications every 10 seconds
        scheduleRepeatedNotifications()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(title: String, content: String): Notification {
        val channelId = "school_plan_service"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "School Plan Service",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notification channel for the School Plan Service"
                    enableVibration(true) // Enable vibration in the channel
                    vibrationPattern = longArrayOf(0, 500, 200, 500) // Vibration pattern
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(longArrayOf(0, 500, 200, 500)) // Vibration pattern
            .build() // Use .build() to get a Notification object
    }

    private fun scheduleRepeatedNotifications() {
        // Create a runnable task that sends a notification every 10 seconds
        val notificationRunnable = object : Runnable {
            override fun run() {
                sendNotification()
                // Re-schedule the same task to run after 10 seconds
                handler.postDelayed(this, notificationInterval)
            }
        }

        // Start the first notification after 10 seconds
        handler.postDelayed(notificationRunnable, notificationInterval)
    }

    private fun sendNotification() {
        val currentDateTime = LocalDateTime.now()
        val plan = LekcjeUtils.getSchoolPlan(context = this, "-114", false);
        val periodStartEndList = LekcjeUtils.getStartEndTimes();
        val currentLesson = periodStartEndList.find { a ->
            timeStringToMinutes(a.start) < minutesFromMidnight(currentDateTime) && timeStringToMinutes(
                a.end
            ) > minutesFromMidnight(currentDateTime)
        }
        val lekcjus = plan?.get(2)?.get(currentLesson?.id ?: 0)
        val timeToEnd = timeStringToMinutes(
            currentLesson?.end ?: "00:00"
        ) - minutesFromMidnight(currentDateTime)
        val notification =
            createNotification(
                "Repeated",
                "Trwa lekcja ${currentLesson?.start} ${lekcjus?.subject} ${currentLesson?.end} $timeToEnd"
            )

        if (timeToEnd == 5 || true) {
            createNotification(
                "Koniec zaraz",
                "O ${lekcjus?.subject} ${currentLesson?.end} koniec lekcji"
            )
        }
        // Show the notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions
            return
        }

        // Use a fixed notificationId to overwrite the previous notification
        if ((currentDateTime.second - currentDateTime.second % 10) / 10 % 3 != 0) {
            NotificationManagerCompat.from(this).notify(notificationId, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the callback when the service is destroyed to stop notifications
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
