package com.example.engine

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences
import com.example.data.SpeedPreset
import kotlin.math.sqrt

/**
 * High-performance cursor positioning and physics engine.
 * Computes sub-pixel vectors, acceleration curves, diagonal normalization,
 * and boundary clamping without taxing the CPU when idle.
 */
class CursorEngine(
    private val context: Context,
    private val prefs: KeyCursorPreferences,
    private val onPositionChanged: (x: Float, y: Float) -> Unit
) {
    var screenWidth: Int = 1200
        private set
    var screenHeight: Int = 1920
        private set

    var currentX: Float = prefs.lastCursorX
        private set
    var currentY: Float = prefs.lastCursorY
        private set

    // Active directional states
    private var isUpPressed = false
    private var isDownPressed = false
    private var isLeftPressed = false
    private var isRightPressed = false

    // Modifiers
    private var isShiftFast = false
    private var isCtrlFine = false

    // Acceleration physics
    private var holdDurationFrames = 0
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isLoopRunning = false

    // Inactivity tracking
    private var lastMovementTimestamp = System.currentTimeMillis()

    private val frameRunnable = object : Runnable {
        override fun run() {
            if (!isAnyDirectionPressed()) {
                isLoopRunning = false
                holdDurationFrames = 0
                return
            }

            tickMovement()
            mainHandler.postDelayed(this, 16L) // ~60fps smooth ticker
        }
    }

    fun updateScreenSize(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        screenWidth = width
        screenHeight = height
        CursorStateHolder.setScreenSize(width, height)
        clampToScreen()
        onPositionChanged(currentX, currentY)
    }

    fun setDirectionPressed(up: Boolean, down: Boolean, left: Boolean, right: Boolean) {
        isUpPressed = up
        isDownPressed = down
        isLeftPressed = left
        isRightPressed = right

        if (isAnyDirectionPressed()) {
            lastMovementTimestamp = System.currentTimeMillis()
            if (!isLoopRunning) {
                isLoopRunning = true
                holdDurationFrames = 0
                mainHandler.post(frameRunnable)
            }
        }
    }

    fun setModifiers(shiftFast: Boolean, ctrlFine: Boolean) {
        isShiftFast = shiftFast
        isCtrlFine = ctrlFine
        CursorStateHolder.setFastMode(shiftFast)
        CursorStateHolder.setFineMode(ctrlFine)
    }

    private fun isAnyDirectionPressed(): Boolean {
        return isUpPressed || isDownPressed || isLeftPressed || isRightPressed
    }

    private fun tickMovement() {
        var dx = 0f
        var dy = 0f

        if (isUpPressed) dy -= 1f
        if (isDownPressed) dy += 1f
        if (isLeftPressed) dx -= 1f
        if (isRightPressed) dx += 1f

        if (dx == 0f && dy == 0f) return

        // Diagonal normalization to ensure consistent speed in all directions
        if (dx != 0f && dy != 0f && prefs.diagonalEnabled) {
            val invSqrt2 = 0.70710678f
            dx *= invSqrt2
            dy *= invSqrt2
        }

        // Calculate speed with acceleration and presets
        holdDurationFrames++
        val baseSpeed = prefs.baseSpeed
        val presetMult = prefs.speedPreset.multiplier

        // Smooth acceleration factor (ramps up over ~15 frames to max acceleration)
        val accelProgress = (holdDurationFrames / 15f).coerceIn(0f, 1f)
        val accelFactor = 1.0f + (prefs.acceleration - 1.0f) * accelProgress

        // Modifier multiplier
        val modMultiplier = when {
            isCtrlFine -> prefs.fineMultiplier
            isShiftFast -> prefs.fastMultiplier
            else -> 1.0f
        }

        val finalSpeed = baseSpeed * presetMult * accelFactor * modMultiplier

        currentX += dx * finalSpeed
        currentY += dy * finalSpeed

        clampToScreen()

        // Update state and notify listener
        CursorStateHolder.setPosition(currentX, currentY)
        onPositionChanged(currentX, currentY)

        // Save position periodically to preferences
        prefs.lastCursorX = currentX
        prefs.lastCursorY = currentY
    }

    private fun clampToScreen() {
        val maxX = (screenWidth - 1).coerceAtLeast(0).toFloat()
        val maxY = (screenHeight - 1).coerceAtLeast(0).toFloat()
        currentX = currentX.coerceIn(0f, maxX)
        currentY = currentY.coerceIn(0f, maxY)
    }

    fun recenter() {
        currentX = screenWidth / 2f
        currentY = screenHeight / 2f
        prefs.lastCursorX = currentX
        prefs.lastCursorY = currentY
        CursorStateHolder.setPosition(currentX, currentY)
        onPositionChanged(currentX, currentY)
    }

    fun stopMovement() {
        isUpPressed = false
        isDownPressed = false
        isLeftPressed = false
        isRightPressed = false
        isLoopRunning = false
        holdDurationFrames = 0
        mainHandler.removeCallbacks(frameRunnable)
    }

    fun checkInactivity(): Boolean {
        if (prefs.inactivityTimeoutSeconds <= 0) return false
        val idleMs = System.currentTimeMillis() - lastMovementTimestamp
        return idleMs > (prefs.inactivityTimeoutSeconds * 1000L)
    }
}
