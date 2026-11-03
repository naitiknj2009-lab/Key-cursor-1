package com.example.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences

/**
 * Manages the floating cursor window lifecycle via Android WindowManager.
 * Uses TYPE_ACCESSIBILITY_OVERLAY for zero-permission overlay capability within
 * AccessibilityService, with TYPE_APPLICATION_OVERLAY fallback.
 */
class CursorOverlayManager(
    private val context: Context,
    private val prefs: KeyCursorPreferences
) {
    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var cursorView: CursorView? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var isAttached = false

    fun show() {
        if (isAttached) return

        val view = CursorView(context, prefs)
        cursorView = view

        val sizePx = ((prefs.cursorSizeDp + 30) * context.resources.displayMetrics.density).toInt()

        val windowType = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY

        val params = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = prefs.lastCursorX.toInt()
            y = prefs.lastCursorY.toInt()
        }

        layoutParams = params

        try {
            windowManager.addView(view, params)
            isAttached = true
            view.visibility = if (prefs.cursorVisibility) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            // Fallback for application overlay if accessibility overlay fails
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    windowManager.addView(view, params)
                    isAttached = true
                }
            } catch (fallbackError: Exception) {
                fallbackError.printStackTrace()
            }
        }
    }

    fun updatePosition(x: Float, y: Float) {
        val params = layoutParams ?: return
        val view = cursorView ?: return
        if (!isAttached) return

        params.x = x.toInt()
        params.y = y.toInt()

        try {
            windowManager.updateViewLayout(view, params)
        } catch (e: Exception) {
            // Ignore temporary window update errors during transitions
        }
    }

    fun triggerClick() {
        cursorView?.triggerClickAnimation()
    }

    fun setDragging(dragging: Boolean) {
        cursorView?.setDraggingState(dragging)
    }

    fun setVisibility(visible: Boolean) {
        prefs.cursorVisibility = visible
        cursorView?.visibility = if (visible) View.VISIBLE else View.GONE
        CursorStateHolder.setCursorVisible(visible)
    }

    fun refreshStyle() {
        cursorView?.invalidate()
    }

    fun hide() {
        if (!isAttached) return
        try {
            cursorView?.let { windowManager.removeView(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursorView = null
            layoutParams = null
            isAttached = false
        }
    }
}
