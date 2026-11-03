package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.CursorStateHolder

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "keycursor_foreground_channel"
        const val NOTIFICATION_ID = 2001

        const val ACTION_TOGGLE_CURSOR = "com.example.keycursor.ACTION_TOGGLE_CURSOR"
        const val ACTION_CYCLE_SPEED = "com.example.keycursor.ACTION_CYCLE_SPEED"
        const val ACTION_TOGGLE_VISIBILITY = "com.example.keycursor.ACTION_TOGGLE_VISIBILITY"
        const val ACTION_STOP_SERVICE = "com.example.keycursor.ACTION_STOP_SERVICE"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(): Notification {
        val state = CursorStateHolder.state.value

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleIntent = Intent(context, CursorAccessibilityService::class.java).apply {
            action = ACTION_TOGGLE_CURSOR
        }
        val togglePending = PendingIntent.getService(
            context,
            1,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val speedIntent = Intent(context, CursorAccessibilityService::class.java).apply {
            action = ACTION_CYCLE_SPEED
        }
        val speedPending = PendingIntent.getService(
            context,
            2,
            speedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val visibilityIntent = Intent(context, CursorAccessibilityService::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
        }
        val visibilityPending = PendingIntent.getService(
            context,
            3,
            visibilityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusText = when {
            state.isTypingMode -> "Typing Mode active (Keys pass through)"
            state.isCursorActive -> "Cursor Active (Speed: ${state.speedPreset.displayName})"
            else -> "Cursor Paused"
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("KeyCursor Active")
            .setContentText(statusText)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                android.R.drawable.ic_media_play,
                if (state.isCursorActive) "Pause" else "Resume",
                togglePending
            )
            .addAction(
                android.R.drawable.ic_menu_agenda,
                "Speed",
                speedPending
            )
            .addAction(
                android.R.drawable.ic_menu_view,
                if (state.isCursorVisible) "Hide" else "Show",
                visibilityPending
            )
            .build()
    }

    fun updateNotification() {
        try {
            notificationManager.notify(NOTIFICATION_ID, buildNotification())
        } catch (e: Exception) {
            // Ignore if notification cannot be updated
        }
    }
}
