package com.wakeme.app

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WakeViewModel : ViewModel() {
    var caffeineMode by mutableStateOf(false)
    val logs = mutableStateListOf<String>()
    private var appContext: Context? = null
    private var prefs: SharedPreferences? = null

    // Listener that detects changes made by the Tile or Widget
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
        if (key == "wake_enabled") {
            val isEnabled = sharedPrefs.getBoolean(key, false)
            caffeineMode = isEnabled
            log(if (isEnabled) "[Sync] Tile/Widget turned ON" else "[Sync] Tile/Widget turned OFF")
        }
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = appContext?.getSharedPreferences("wakeme_prefs", Context.MODE_PRIVATE)
        
        // Initial sync
        caffeineMode = prefs?.getBoolean("wake_enabled", false) ?: false
        
        // Start listening for background changes
        prefs?.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun updateCaffeineMode(enabled: Boolean) {
        val ctx = appContext ?: return
        
        // 1. Save to prefs so Tile/Widget stay in sync
        prefs?.edit()?.putBoolean("wake_enabled", enabled)?.apply()
        
        // 2. Start/Stop the Service (The Service handles the actual WakeLock)
        val intent = Intent(ctx, WakeService::class.java).apply {
            action = if (enabled) "START_WAKE" else "STOP_WAKE"
        }
        
        if (enabled) {
            ctx.startForegroundService(intent)
        } else {
            ctx.stopService(intent)
        }
        
        log(if (enabled) "[App] Requesting WakeLock..." else "[App] Releasing WakeLock...")
    }

    fun log(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            logs.add(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        prefs?.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }
}