package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences
import com.example.data.SpeedPreset
import com.example.engine.CursorEngine
import com.example.engine.GestureEngine
import com.example.engine.KeyboardDeviceManager
import com.example.engine.KeyboardInputHandler
import com.example.overlay.CursorOverlayManager
import com.example.overlay.FloatingPanelManager

class CursorAccessibilityService : AccessibilityService() {

    private lateinit var prefs: KeyCursorPreferences
    private lateinit var cursorEngine: CursorEngine
    private lateinit var gestureEngine: GestureEngine
    private lateinit var inputHandler: KeyboardInputHandler
    private lateinit var overlayManager: CursorOverlayManager
    private lateinit var panelManager: FloatingPanelManager
    private lateinit var keyboardManager: KeyboardDeviceManager
    private lateinit var notificationHelper: NotificationHelper

    companion object {
        var instance: CursorAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        prefs = KeyCursorPreferences(this)
        notificationHelper = NotificationHelper(this)

        updateScreenMetrics()

        cursorEngine = CursorEngine(this, prefs) { x, y ->
            overlayManager.updatePosition(x, y)
        }
        cursorEngine.updateScreenSize(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )

        gestureEngine = GestureEngine(this, prefs)

        inputHandler = KeyboardInputHandler(prefs, cursorEngine, gestureEngine) { isTyping ->
            val modeText = if (isTyping) "KeyCursor: Typing Mode (Keys pass through)" else "KeyCursor: Mouse Mode"
            Toast.makeText(this, modeText, Toast.LENGTH_SHORT).show()
            notificationHelper.updateNotification()
        }

        overlayManager = CursorOverlayManager(this, prefs)

        panelManager = FloatingPanelManager(
            context = this,
            prefs = prefs,
            onToggleCursor = { toggleCursorActive() },
            onToggleTypingMode = { inputHandler.toggleTypingMode() },
            onToggleVisibility = { toggleVisibility() },
            onCycleSpeed = { cycleSpeedPreset() },
            onRecenterCursor = { recenterCursor() },
            onLeftClick = { performFloatingLeftClick() },
            onRightClick = { performFloatingRightClick() },
            onSpeedChanged = { speed -> setSpeed(speed) }
        )

        keyboardManager = KeyboardDeviceManager(this) { connected, deviceName ->
            if (connected && prefs.autoActivateOnConnect) {
                prefs.isCursorEnabled = true
                CursorStateHolder.setCursorActive(true)
                Toast.makeText(this, "Keyboard connected: $deviceName. Cursor activated.", Toast.LENGTH_SHORT).show()
            } else if (!connected && prefs.autoDeactivateOnDisconnect) {
                prefs.isCursorEnabled = false
                CursorStateHolder.setCursorActive(false)
                Toast.makeText(this, "Keyboard disconnected. Cursor deactivated.", Toast.LENGTH_SHORT).show()
            }
            notificationHelper.updateNotification()
        }
        keyboardManager.startListening()

        if (prefs.isCursorEnabled) {
            overlayManager.show()
        }

        if (prefs.isPanelEnabled) {
            panelManager.show()
        }

        CursorStateHolder.setServiceRunning(true)
        CursorStateHolder.setCursorActive(prefs.isCursorEnabled)
        CursorStateHolder.setSpeedPreset(prefs.speedPreset)

        notificationHelper.updateNotification()
    }

    private fun updateScreenMetrics() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = wm.currentWindowMetrics.bounds
            CursorStateHolder.setScreenSize(bounds.width(), bounds.height())
        } else {
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(metrics)
            CursorStateHolder.setScreenSize(metrics.widthPixels, metrics.heightPixels)
        }
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        val handled = inputHandler.handleKeyEvent(event)
        if (handled && CursorStateHolder.state.value.isClicking) {
            overlayManager.triggerClick()
        }
        overlayManager.setDragging(CursorStateHolder.state.value.isDragging)
        return handled
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            NotificationHelper.ACTION_TOGGLE_CURSOR -> toggleCursorActive()
            NotificationHelper.ACTION_CYCLE_SPEED -> cycleSpeedPreset()
            NotificationHelper.ACTION_TOGGLE_VISIBILITY -> toggleVisibility()
        }
        return START_STICKY
    }

    fun toggleCursorActive() {
        val newState = !prefs.isCursorEnabled
        prefs.isCursorEnabled = newState
        CursorStateHolder.setCursorActive(newState)
        if (newState) {
            overlayManager.show()
            Toast.makeText(this, "KeyCursor Active", Toast.LENGTH_SHORT).show()
        } else {
            inputHandler.resetDirectionalKeys()
            overlayManager.hide()
            Toast.makeText(this, "KeyCursor Paused", Toast.LENGTH_SHORT).show()
        }
        notificationHelper.updateNotification()
    }

    fun cycleSpeedPreset() {
        val next = when (prefs.speedPreset) {
            SpeedPreset.SLOW -> SpeedPreset.NORMAL
            SpeedPreset.NORMAL -> SpeedPreset.FAST
            SpeedPreset.FAST -> SpeedPreset.SLOW
        }
        prefs.speedPreset = next
        CursorStateHolder.setSpeedPreset(next)
        Toast.makeText(this, "Speed: ${next.displayName}", Toast.LENGTH_SHORT).show()
        notificationHelper.updateNotification()
    }

    fun toggleVisibility() {
        val next = !prefs.cursorVisibility
        overlayManager.setVisibility(next)
        Toast.makeText(this, if (next) "Cursor Visible" else "Cursor Hidden", Toast.LENGTH_SHORT).show()
        notificationHelper.updateNotification()
    }

    fun refreshCursorStyle() {
        overlayManager.refreshStyle()
    }

    fun reloadFloatingPanel() {
        panelManager.hide()
        if (prefs.isPanelEnabled) {
            panelManager.show()
        }
    }

    fun recenterCursor() {
        cursorEngine.recenter()
        Toast.makeText(this, "Cursor centered", Toast.LENGTH_SHORT).show()
    }

    fun performFloatingLeftClick() {
        val clickX = cursorEngine.currentX
        val clickY = cursorEngine.currentY
        gestureEngine.performLeftClick(clickX, clickY)
        overlayManager.triggerClick()
    }

    fun performFloatingRightClick() {
        val clickX = cursorEngine.currentX
        val clickY = cursorEngine.currentY
        gestureEngine.performRightClick(clickX, clickY)
        overlayManager.triggerClick()
    }

    fun setSpeed(speed: Float) {
        prefs.baseSpeed = speed.coerceIn(4f, 40f)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateScreenMetrics()
        val metrics = resources.displayMetrics
        cursorEngine.updateScreenSize(metrics.widthPixels, metrics.heightPixels)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Event monitoring if needed for window focus changes
    }

    override fun onInterrupt() {
        inputHandler.resetDirectionalKeys()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        CursorStateHolder.setServiceRunning(false)
        keyboardManager.stopListening()
        overlayManager.hide()
        panelManager.hide()
        inputHandler.resetDirectionalKeys()
    }
}
