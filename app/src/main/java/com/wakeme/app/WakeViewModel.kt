package com.wakeme.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
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

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
        if (key == "wake_enabled") {
            caffeineMode = sharedPrefs.getBoolean(key, false)
        }
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = appContext?.getSharedPreferences("wakeme_prefs", Context.MODE_PRIVATE)
        caffeineMode = prefs?.getBoolean("wake_enabled", false) ?: false
        prefs?.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    // THIS IS THE UPDATED TOGGLE LOGIC
    fun updateCaffeineMode(enabled: Boolean) {
        val ctx = appContext ?: return

        if (!enabled) {
            // Turning it off never requires a permission check
            performToggle(false)
        } else {
            // Turning it on triggers the permission check
            if (Settings.System.canWrite(ctx)) {
                performToggle(true)
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data = Uri.parse("package:${ctx.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                ctx.startActivity(intent)
                log("[System] Permission required: Modify System Settings")
            }
        }
    }

    private fun performToggle(enabled: Boolean) {
        val ctx = appContext ?: return
        prefs?.edit()?.putBoolean("wake_enabled", enabled)?.apply()
        
        val intent = Intent(ctx, WakeService::class.java).apply {
            action = if (enabled) "START_WAKE" else "STOP_WAKE"
        }
        
        if (enabled) {
            ctx.startForegroundService(intent)
        } else {
            ctx.stopService(intent)
        }
        
        log(if (enabled) "[App] Caffeine Mode: ON" else "[App] Caffeine Mode: OFF")
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