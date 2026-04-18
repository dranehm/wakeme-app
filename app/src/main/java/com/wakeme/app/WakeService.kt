package com.wakeme.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat

class WakeService : Service() {
    private val CHANNEL_ID = "wakeme_channel"
    private var originalTimeout: Int = 30000 // Default 30s fallback

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_WAKE" -> startWakeLock()
            "STOP_WAKE" -> stopWakeLock()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startWakeLock() {
        if (Settings.System.canWrite(this)) {
            // Save current user timeout
            originalTimeout = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 30000)
            
            // Set timeout to 24 hours (Infinite effect)
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 24 * 60 * 60 * 1000)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Wake Me is Active")
            .setContentText("System timeout extended to 24h")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }
    }

    private fun stopWakeLock() {
        if (Settings.System.canWrite(this)) {
            // Restore user's original timeout
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, originalTimeout)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Wake Me Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        if (Settings.System.canWrite(this)) {
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, originalTimeout)
        }
        super.onDestroy()
    }
}