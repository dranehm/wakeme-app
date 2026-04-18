package com.wakeme.app

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.content.Intent
import androidx.core.content.ContextCompat.startForegroundService

class WakeTileService : TileService() {

    override fun onClick() {
        super.onClick()
        val enabled = !WakeWidget.getWakeState(this)
        WakeWidget.setWakeState(this, enabled)
        updateTile(enabled)
        val serviceIntent = Intent(this, WakeService::class.java).apply {
            action = if (enabled) "START_WAKE" else "STOP_WAKE"
        }
        if (enabled) {
            startForegroundService(this, serviceIntent)
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
        val tile = qsTile
        tile.state = if (enabled) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.label = "Wake Me"
        tile.contentDescription = if (enabled) "Screen awake - tap to stop" else "Screen sleep - tap to wake"
        tile.updateTile()
    }
}
