package com.example.engine

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences

/**
 * Handles real touch and pointer interactions using Android AccessibilityService.dispatchGesture().
 * Dispatches real touches at current cursor coordinates for left-click, double-click,
 * right-click (context menu gesture), continuous drag-and-drop, long-press, and scrolling.
 */
class GestureEngine(
    private val service: AccessibilityService,
    private val prefs: KeyCursorPreferences
) {
    companion object {
        private const val TAG = "KeyCursorGesture"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val vibrator: Vibrator? =
        service.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    // Drag tracking
    private var isDragging = false
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var activeDragStroke: GestureDescription.StrokeDescription? = null

    private fun vibrate(durationMs: Long = 18L) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore if vibration fails
        }
    }

    /**
     * Dispatches a real left click (single tap) at the specified coordinates.
     */
    fun performLeftClick(x: Float, y: Float, onComplete: (() -> Unit)? = null) {
        vibrate(20L)
        CursorStateHolder.setClicking(true)
        CursorStateHolder.setAction("Left Click at (${x.toInt()}, ${y.toInt()})")

        val path = Path().apply {
            moveTo(x, y)
            lineTo(x, y)
        }

        val stroke = GestureDescription.StrokeDescription(path, 0, prefs.clickDurationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        service.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                mainHandler.postDelayed({
                    CursorStateHolder.setClicking(false)
                    onComplete?.invoke()
                }, 100L)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                CursorStateHolder.setClicking(false)
                Log.w(TAG, "Left click gesture cancelled by system")
            }
        }, null)
    }

    /**
     * Dispatches a right-click equivalent.
     * Note on Android platform limitation:
     * Android does not have a separate secondary click gesture in dispatchGesture.
     * Right-click is universally represented in Android touch by a contextual long-press
     * (450ms) which triggers context menus, copy/paste bars, or app-specific popup menus.
     */
    fun performRightClick(x: Float, y: Float) {
        vibrate(35L)
        CursorStateHolder.setClicking(true)
        CursorStateHolder.setAction("Context Menu (Right Click) at (${x.toInt()}, ${y.toInt()})")

        val path = Path().apply {
            moveTo(x, y)
            lineTo(x, y)
        }

        // Long press triggers Android context menus & selection handles
        val stroke = GestureDescription.StrokeDescription(path, 0, 480L)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        service.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                mainHandler.postDelayed({
                    CursorStateHolder.setClicking(false)
                }, 80L)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                CursorStateHolder.setClicking(false)
            }
        }, null)
    }

    /**
     * Dispatches a double click (two rapid taps with configurable interval).
     */
    fun performDoubleClick(x: Float, y: Float) {
        if (!prefs.doubleClickEnabled) {
            performLeftClick(x, y)
            return
        }

        vibrate(15L)
        CursorStateHolder.setClicking(true)
        CursorStateHolder.setAction("Double Click at (${x.toInt()}, ${y.toInt()})")

        val path = Path().apply {
            moveTo(x, y)
            lineTo(x, y)
        }

        val tap1 = GestureDescription.StrokeDescription(path, 0, 40L)
        val tap2 = GestureDescription.StrokeDescription(path, 100L, 40L)
        val gesture = GestureDescription.Builder()
            .addStroke(tap1)
            .addStroke(tap2)
            .build()

        service.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                mainHandler.postDelayed({
                    CursorStateHolder.setClicking(false)
                }, 100L)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                CursorStateHolder.setClicking(false)
            }
        }, null)
    }

    /**
     * Dispatches a dedicated long-press.
     */
    fun performLongPress(x: Float, y: Float) {
        vibrate(40L)
        CursorStateHolder.setClicking(true)
        CursorStateHolder.setAction("Long Press (${prefs.longPressDurationMs}ms) at (${x.toInt()}, ${y.toInt()})")

        val path = Path().apply {
            moveTo(x, y)
            lineTo(x, y)
        }

        val stroke = GestureDescription.StrokeDescription(path, 0, prefs.longPressDurationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        service.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                mainHandler.postDelayed({
                    CursorStateHolder.setClicking(false)
                }, 80L)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                CursorStateHolder.setClicking(false)
            }
        }, null)
    }

    /**
     * Starts continuous drag-and-drop gesture.
     */
    fun startDrag(x: Float, y: Float) {
        if (isDragging) return
        isDragging = true
        dragStartX = x
        dragStartY = y
        vibrate(30L)
        CursorStateHolder.setDragging(true)
        CursorStateHolder.setAction("Drag Started from (${x.toInt()}, ${y.toInt()})")
    }

    /**
     * Ends drag gesture by executing continuous swipe gesture from start to destination.
     */
    fun endDrag(currentX: Float, currentY: Float) {
        if (!isDragging) return
        isDragging = false
        vibrate(25L)
        CursorStateHolder.setDragging(false)
        CursorStateHolder.setAction("Drag Dropped to (${currentX.toInt()}, ${currentY.toInt()})")

        val path = Path().apply {
            moveTo(dragStartX, dragStartY)
            lineTo(currentX, currentY)
        }

        // Complete the stroke
        val duration = (300L + (Math.hypot((currentX - dragStartX).toDouble(), (currentY - dragStartY).toDouble()) * 0.8)).toLong().coerceIn(250L, 800L)
        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        service.dispatchGesture(gesture, null, null)
    }

    fun isDraggingActive(): Boolean = isDragging

    /**
     * Executes the configured middle-click action.
     */
    fun performMiddleClick(x: Float, y: Float) {
        vibrate(28L)
        when (prefs.middleClickAction) {
            com.example.data.MiddleClickAction.SYSTEM_BACK -> {
                CursorStateHolder.setAction("Middle Click: System Back")
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            }
            com.example.data.MiddleClickAction.SYSTEM_HOME -> {
                CursorStateHolder.setAction("Middle Click: System Home")
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            }
            com.example.data.MiddleClickAction.NOTIFICATIONS -> {
                CursorStateHolder.setAction("Middle Click: Notifications Shade")
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
            }
            com.example.data.MiddleClickAction.RECENTS -> {
                CursorStateHolder.setAction("Middle Click: Recent Apps Overview")
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
            }
            com.example.data.MiddleClickAction.DOUBLE_CLICK -> {
                performDoubleClick(x, y)
            }
        }
    }

    /**
     * Dispatches vertical scroll swipe.
     * up = true scrolls content up (finger swipes down).
     * up = false scrolls content down (finger swipes up).
     */
    fun performVerticalScroll(cursorX: Float, cursorY: Float, up: Boolean) {
        vibrate(12L)
        val actualUp = if (prefs.invertScrollDirection) !up else up
        val distance = (prefs.scrollStepPx * prefs.scrollSpeed).coerceIn(80f, 800f)
        val startY = cursorY
        val endY = if (actualUp) {
            (cursorY + distance).coerceAtMost(CursorStateHolder.state.value.screenHeight - 10f)
        } else {
            (cursorY - distance).coerceAtLeast(10f)
        }

        CursorStateHolder.setAction(if (actualUp) "Scroll Up" else "Scroll Down")

        val path = Path().apply {
            moveTo(cursorX, startY)
            lineTo(cursorX, endY)
        }

        val stroke = GestureDescription.StrokeDescription(path, 0, 160L)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        service.dispatchGesture(gesture, null, null)
    }

    /**
     * Dispatches horizontal scroll swipe.
     */
    fun performHorizontalScroll(cursorX: Float, cursorY: Float, left: Boolean) {
        vibrate(12L)
        val distance = (prefs.scrollStepPx * prefs.scrollSpeed).coerceIn(80f, 600f)
        val startX = cursorX
        val endX = if (left) {
            (cursorX + distance).coerceAtMost(CursorStateHolder.state.value.screenWidth - 10f)
        } else {
            (cursorX - distance).coerceAtLeast(10f)
        }

        CursorStateHolder.setAction(if (left) "Scroll Left" else "Scroll Right")

        val path = Path().apply {
            moveTo(startX, cursorY)
            lineTo(endX, cursorY)
        }

        val stroke = GestureDescription.StrokeDescription(path, 0, 160L)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        service.dispatchGesture(gesture, null, null)
    }
}
