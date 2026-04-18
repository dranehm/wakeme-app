package com.wakeme.app

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat.startForegroundService

class WakeTileService : TileService() {

    override fun onClick() {
        super.onClick()

        // 1. Permission Check
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            // This collapses the notification tray and opens Settings
            startActivityAndCollapse(intent)
            return
        }

        // 2. Logic if permission is granted
        val enabled = !WakeWidget.getWakeState(this)
        WakeWidget.setWakeState(this, enabled)
        updateTile(enabled)

        val serviceIntent = Intent(this, WakeService::class.java).apply {
            action = if (enabled) "START_WAKE" else "STOP_WAKE"
        }

        if (enabled) {
            applicationContext.startForegroundService(serviceIntent)
        } else {
            stopService(serviceIntent)
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTile(WakeWidget.getWakeState(this))
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile(WakeWidget.getWakeState(this))
    }

    private fun updateTile(enabled: Boolean) {
        val tile = qsTile ?: return
        tile.state = if (enabled) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.label = "Wake Me"
        tile.updateTile()
    }
}