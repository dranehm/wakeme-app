package com.wakeme.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import androidx.core.content.ContextCompat.startForegroundService

class WakeWidget : AppWidgetProvider() {

    companion object {
        private const val PREFS_NAME = "wakeme_prefs"
        private const val KEY_ENABLED = "wake_enabled"
        fun getWakeState(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_ENABLED, false)
        }
        fun setWakeState(context: Context, enabled: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_wake)
        val enabled = getWakeState(context)

        // Toggle pending intent
        val toggleIntent = Intent(context, WakeWidget::class.java).apply {
            action = "TOGGLE_WAKE"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        views.setOnClickPendingIntent(R.id.widget_button, togglePendingIntent)
        views.setTextViewText(R.id.widget_status, if (enabled) "ON" else "OFF")
        views.setInt(R.id.widget_button, "setBackgroundColor", 
            if (enabled) context.resources.getColor(android.R.color.holo_green_dark) else context.resources.getColor(android.R.color.holo_red_dark))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

     override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val ctx = context ?: return
        
        if (intent?.action == "TOGGLE_WAKE") {
            val enabled = !getWakeState(ctx)
            setWakeState(ctx, enabled)
            
            val serviceIntent = Intent(ctx, WakeService::class.java).apply {
                action = if (enabled) "START_WAKE" else "STOP_WAKE"
            }
            
            if (enabled) {
                startForegroundService(ctx, serviceIntent)
            } else {
                ctx.stopService(serviceIntent)
            }

            // Refresh the UI on all instances of this widget
            val appWidgetManager = AppWidgetManager.getInstance(ctx)
            val componentName = ComponentName(ctx, WakeWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            for (id in appWidgetIds) {
                updateAppWidget(ctx, appWidgetManager, id)
            }
        }
    }
}
