package com.wakeme.app

import android.content.Context
import android.os.PowerManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WakeViewModel : ViewModel() {
    var caffeineMode by mutableStateOf(false)
    val logs = mutableStateListOf<String>()
    private var wakeLock: PowerManager.WakeLock? = null
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun setCaffeineMode(enabled: Boolean) {
        caffeineMode = enabled
        if (enabled) {
            acquireWakeLock()
        } else {
            releaseWakeLock()
        }
        log(if (enabled) "[WakeLock] Enabled - Screen awake" else "[WakeLock] Disabled")
    }

    private fun acquireWakeLock() {
        val ctx = appContext ?: return
        try {
            val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock?.release()
            wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "WakeMe::ScreenWakeLock"
            ).apply {
                acquire(10*60*60*1000L /* 10hr timeout */)
            }
            log("[WakeLock] PARTIAL acquired")
        } catch (e: Exception) {
            log("[WakeLock] Acquire error: ${e.message}")
        }
    }

    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                    log("[WakeLock] Released")
                }
            }
            wakeLock = null
        } catch (e: Exception) {
            log("[WakeLock] Release error: ${e.message}")
        }
    }

    private fun log(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            logs.add(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        releaseWakeLock()
    }
}
