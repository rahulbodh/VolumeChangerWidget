package com.rahul.volumeaccessapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.widget.RemoteViews

class VolumeControlWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val ACTION_VOLUME_UP = "com.rahul.volumeaccessapp.VOLUME_UP"
        private const val ACTION_VOLUME_DOWN = "com.rahul.volumeaccessapp.VOLUME_DOWN"
        private const val ACTION_MUTE_UNMUTE = "com.rahul.volumeaccessapp.MUTE_UNMUTE"

        @SuppressLint("RemoteViewLayout")
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_control_layout)

            // Volume Up Intent
            val volumeUpIntent = Intent(context, VolumeControlWidget::class.java).apply {
                action = ACTION_VOLUME_UP
            }
            val pendingVolumeUpIntent = PendingIntent.getBroadcast(context, 0, volumeUpIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_volume_up, pendingVolumeUpIntent)

            // Volume Down Intent
            val volumeDownIntent = Intent(context, VolumeControlWidget::class.java).apply {
                action = ACTION_VOLUME_DOWN
            }
            val pendingVolumeDownIntent = PendingIntent.getBroadcast(context, 0, volumeDownIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_volume_down, pendingVolumeDownIntent)

            // Mute/Unmute Intent
            val muteUnmuteIntent = Intent(context, VolumeControlWidget::class.java).apply {
                action = ACTION_MUTE_UNMUTE
            }
            val pendingMuteUnmuteIntent = PendingIntent.getBroadcast(context, 0, muteUnmuteIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_mute_unmute, pendingMuteUnmuteIntent)

            // Apply the updates to the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        context?.let {
            val audioManager = it.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            // Log the received action for debugging
            Log.d("VolumeControlWidget", "Received action: ${intent?.action}")

            when (intent?.action) {
                ACTION_VOLUME_UP -> {
                    Log.d("VolumeControlWidget", "Volume Up Button Clicked")
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                }
                ACTION_VOLUME_DOWN -> {
                    Log.d("VolumeControlWidget", "Volume Down Button Clicked")
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                }
                ACTION_MUTE_UNMUTE -> {
                    Log.d("VolumeControlWidget", "Mute/Unmute Button Clicked")
                    val isMuted = audioManager.isStreamMute(AudioManager.STREAM_MUSIC)
                    if (isMuted) {
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
                    } else {
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
                    }
                }
            }
        }
    }
}
