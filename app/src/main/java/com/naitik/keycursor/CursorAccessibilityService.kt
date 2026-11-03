package com.naitik.keycursor

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import kotlin.math.sqrt

class CursorAccessibilityService : AccessibilityService() {
    private lateinit var windowManager: WindowManager
    private var cursorView: CursorView? = null
    private var cursorParams: WindowManager.LayoutParams? = null
    private var hudView: HudView? = null
    private var hudParams: WindowManager.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())
    private val held = mutableSetOf<Int>()
    private var cursorX = 0f
    private var cursorY = 0f
    private var dragging = false
    private var typingMode = false
    private var lastEnterAt = 0L

    private val movement = object : Runnable {
        override fun run() {
            if (held.isEmpty()) return
            val prefs = getSharedPreferences("keycursor", MODE_PRIVATE)
            val base = prefs.getFloat("speed", 1f) * 7.5f
            var dx = 0f
            var dy = 0f
            if (KeyEvent.KEYCODE_DPAD_LEFT in held) dx -= 1f
            if (KeyEvent.KEYCODE_DPAD_RIGHT in held) dx += 1f
            if (KeyEvent.KEYCODE_DPAD_UP in held) dy -= 1f
            if (KeyEvent.KEYCODE_DPAD_DOWN in held) dy += 1f
            val magnitude = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
            val multiplier = when {
                held.contains(KeyEvent.KEYCODE_SHIFT_LEFT) || held.contains(KeyEvent.KEYCODE_SHIFT_RIGHT) -> 2.4f
                held.contains(KeyEvent.KEYCODE_CTRL_LEFT) || held.contains(KeyEvent.KEYCODE_CTRL_RIGHT) -> .35f
                else -> 1f
            }
            val metrics = resources.displayMetrics
            val nextX = (cursorX + dx / magnitude * base * multiplier).coerceIn(0f, metrics.widthPixels.toFloat())
            val nextY = (cursorY + dy / magnitude * base * multiplier).coerceIn(0f, metrics.heightPixels.toFloat())
            if (nextX != cursorX || nextY != cursorY) {
                if (dragging) dispatchTouch(cursorX, cursorY, nextX, nextY, 55L)
                cursorX = nextX
                cursorY = nextY
                updateCursor()
                prefs.edit().putFloat("x", cursorX).putFloat("y", cursorY).apply()
            }
            handler.postDelayed(this, 16L)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val prefs = getSharedPreferences("keycursor", MODE_PRIVATE)
        cursorX = prefs.getFloat("x", resources.displayMetrics.widthPixels / 2f)
        cursorY = prefs.getFloat("y", resources.displayMetrics.heightPixels / 2f)
        typingMode = prefs.getBoolean("typing", false)
        addCursor()
        if (prefs.getBoolean("hud", true)) addHud()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() { held.clear(); handler.removeCallbacks(movement) }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val key = event.keyCode
        if (key == KeyEvent.KEYCODE_C && event.isAltPressed && event.action == KeyEvent.ACTION_DOWN) {
            typingMode = !typingMode
            getSharedPreferences("keycursor", MODE_PRIVATE).edit().putBoolean("typing", typingMode).apply()
            hudView?.invalidate()
            return true
        }
        if (typingMode) return false
        val arrows = setOf(KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT)
        val supported = key in arrows || key in setOf(KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_PAGE_UP, KeyEvent.KEYCODE_PAGE_DOWN)
        if (!supported) return false
        if (event.action == KeyEvent.ACTION_DOWN) {
            when {
                key in arrows -> {
                    held.add(key)
                    if (held.size == 1) handler.post(movement)
                }
                key == KeyEvent.KEYCODE_ENTER || key == KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                    if (event.isShiftPressed) dispatchLongPress()
                    else {
                        val now = System.currentTimeMillis()
                        dispatchTap()
                        if (now - lastEnterAt < 330L) dispatchTap()
                        lastEnterAt = now
                    }
                }
                key == KeyEvent.KEYCODE_SPACE -> dragging = true
                key == KeyEvent.KEYCODE_PAGE_UP -> dispatchScroll(-1, event.isShiftPressed)
                key == KeyEvent.KEYCODE_PAGE_DOWN -> dispatchScroll(1, event.isShiftPressed)
            }
        } else if (event.action == KeyEvent.ACTION_UP) {
            held.remove(key)
            if (held.isEmpty()) handler.removeCallbacks(movement)
            if (key == KeyEvent.KEYCODE_SPACE) dragging = false
        }
        return true
    }

    private fun addCursor() {
        if (cursorView != null) return
        val view = CursorView()
        val params = WindowManager.LayoutParams(
            64, 64, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (cursorX - 32).toInt()
            y = (cursorY - 32).toInt()
        }
        runCatching { windowManager.addView(view, params); cursorView = view; cursorParams = params }
    }

    private fun updateCursor() {
        val view = cursorView ?: return
        val params = cursorParams ?: return
        params.x = (cursorX - 32).toInt()
        params.y = (cursorY - 32).toInt()
        runCatching { windowManager.updateViewLayout(view, params); view.invalidate() }
    }

    private fun addHud() {
        if (hudView != null) return
        val view = HudView()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 18; y = 110 }
        runCatching { windowManager.addView(view, params); hudView = view; hudParams = params }
    }

    private fun dispatchTap() = dispatchTouch(cursorX, cursorY, cursorX, cursorY, 45L)
    private fun dispatchLongPress() = dispatchTouch(cursorX, cursorY, cursorX, cursorY, 520L)

    private fun dispatchScroll(direction: Int, horizontal: Boolean) {
        val cx = resources.displayMetrics.widthPixels / 2f
        val cy = resources.displayMetrics.heightPixels / 2f
        if (horizontal) dispatchTouch(cx, cy, cx - 220f * direction, cy, 240L)
        else dispatchTouch(cx, cy, cx, cy - 220f * direction, 240L)
    }

    private fun dispatchTouch(x1: Float, y1: Float, x2: Float, y2: Float, duration: Long) {
        val path = Path().apply { moveTo(x1, y1); lineTo(x2, y2) }
        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        dispatchGesture(GestureDescription.Builder().addStroke(stroke).build(), null, null)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        runCatching { cursorView?.let(windowManager::removeView); hudView?.let(windowManager::removeView) }
        cursorView = null
        hudView = null
        super.onDestroy()
    }

    private inner class CursorView : View(this@CursorAccessibilityService) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        override fun onDraw(canvas: Canvas) {
            val prefs = getSharedPreferences("keycursor", MODE_PRIVATE)
            val palette = intArrayOf(0xFF5A3026.toInt(), 0xFF4E987B.toInt(), 0xFFE38C4C.toInt(), 0xFFCA6C70.toInt(), 0xFF8A72B7.toInt(), 0xFFFFFFFF.toInt())
            paint.color = palette[0]
            paint.style = Paint.Style.FILL
            val s = prefs.getFloat("size", 32f) / 32f
            canvas.save()
            canvas.scale(s, s, 32f, 32f)
            when (prefs.getInt("style", 0)) {
                1 -> {
                    paint.style = Paint.Style.STROKE; paint.strokeWidth = 3.5f
                    canvas.drawCircle(32f, 32f, 17f, paint); canvas.drawCircle(32f, 32f, 3f, paint)
                }
                2 -> {
                    paint.style = Paint.Style.STROKE; paint.strokeWidth = 3.5f
                    canvas.drawLine(32f, 5f, 32f, 59f, paint); canvas.drawLine(5f, 32f, 59f, 32f, paint); canvas.drawCircle(32f, 32f, 8f, paint)
                }
                else -> {
                    val arrow = Path().apply { moveTo(14f, 6f); lineTo(49f, 39f); lineTo(35f, 40f); lineTo(42f, 56f); lineTo(34f, 60f); lineTo(26f, 43f); lineTo(17f, 54f); close() }
                    canvas.drawPath(arrow, paint)
                }
            }
            canvas.restore()
        }
    }

    private inner class HudView : View(this@CursorAccessibilityService) {
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private val density = resources.displayMetrics.density
        private var lastX = 0f
        private var lastY = 0f
        private var downX = 0f
        private var downY = 0f
        private var draggingPanel = false
        private var activeControl = -1
        private var speedValue = getSharedPreferences("keycursor", MODE_PRIVATE).getFloat("speed", 1f)

        private fun d(v: Float) = v * density
        private fun rounded(c: Canvas, l: Float, t: Float, r: Float, b: Float, radius: Float, color: Int) {
            p.color = color; p.style = Paint.Style.FILL
            c.drawRoundRect(d(l), d(t), d(r), d(b), d(radius), d(radius), p)
        }
        private fun label(c: Canvas, value: String, x: Float, y: Float, size: Float, color: Int, bold: Boolean = false) {
            p.color = color; p.textSize = d(size); p.typeface = if (bold) android.graphics.Typeface.create("sans-serif-medium",  android.graphics.Typeface.BOLD) else android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
            c.drawText(value, d(x), d(y), p)
        }
        override fun onDraw(canvas: Canvas) {
            val w = width / density
            val h = height / density
            // Soft dark glass panel inspired by the supplied Guest Mode control screen.
            rounded(canvas, 3f, 3f, w - 3f, h - 3f, 28f, 0xE92B2929.toInt())
            p.color = 0x44FFFFFF; p.style = Paint.Style.STROKE; p.strokeWidth = d(1f)
            canvas.drawRoundRect(d(3f), d(3f), d(w - 3f), d(h - 3f), d(28f), d(28f), p)
            p.style = Paint.Style.FILL
            // Header
            label(canvas, "‹", 20f, 37f, 27f, 0xFFF5EEE8.toInt())
            label(canvas, "KEYCURSOR", 53f, 29f, 12f, 0xFFF5EEE8.toInt(), true)
            label(canvas, if (typingMode) "Typing mode" else "Pointer mode", 53f, 45f, 10f, 0xFFBEB5AE.toInt())
            rounded(canvas, w - 57f, 17f, w - 19f, 39f, 12f, if (typingMode) 0xFF68615C.toInt() else 0xFFBCEB72.toInt())
            p.color = 0xFFFFFFFF.toInt()
            canvas.drawCircle(d(if (typingMode) w - 45f else w - 31f), d(28f), d(7f), p)
            // Vertical speed slider and percent-like readout
            label(canvas, "POINTER SPEED", 25f, 83f, 9f, 0xFFBEB5AE.toInt(), true)
            val trackX = 45f
            rounded(canvas, trackX - 6f, 104f, trackX + 6f, 264f, 8f, 0x665D5854)
            val ratio = ((speedValue - .5f) / 2f).coerceIn(0f, 1f)
            val knobY = 264f - ratio * 160f
            rounded(canvas, trackX - 6f, knobY, trackX + 6f, 264f, 8f, 0xFFFFD98F.toInt())
            rounded(canvas, 72f, knobY - 17f, 121f, knobY + 17f, 18f, 0xFF5B5550.toInt())
            label(canvas, "${(speedValue * 100).toInt()}%", 82f, knobY + 5f, 12f, 0xFFFFF5E9.toInt(), true)
            label(canvas, "SLOW", 28f, 284f, 8f, 0xFFBEB5AE.toInt())
            label(canvas, "FAST", 28f, 98f, 8f, 0xFFBEB5AE.toInt())
            // Mode / action cards along bottom, like the reference's device controls.
            label(canvas, "QUICK CONTROLS", 22f, 317f, 9f, 0xFFBEB5AE.toInt(), true)
            val names = listOf("Move", "Click", "Drag")
            val symbols = listOf("↔", "●", "⇱")
            for (i in 0..2) {
                val left = 17f + i * 83f
                rounded(canvas, left, 333f, left + 75f, 405f, 13f, if (activeControl == i) 0xFFD9F78D.toInt() else 0xFF484440.toInt())
                label(canvas, symbols[i], left + 27f, 363f, 18f, if (activeControl == i) 0xFF342A25.toInt() else 0xFFF5EEE8.toInt(), true)
                label(canvas, names[i], left + 18f, 389f, 10f, if (activeControl == i) 0xFF342A25.toInt() else 0xFFD4CCC5.toInt(), true)
            }
            label(canvas, "Drag panel to reposition", 70f, h - 12f, 8f, 0xFFBEB5AE.toInt())
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val params = hudParams ?: return true
            val x = event.x / density
            val y = event.y / density
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX; lastY = event.rawY
                    downX = x; downY = y; draggingPanel = false
                    activeControl = when {
                        y in 333f..410f && x in 17f..92f -> 0
                        y in 333f..410f && x in 100f..175f -> 1
                        y in 333f..410f && x in 183f..258f -> 2
                        else -> -1
                    }
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - lastX).toInt()
                    val dy = (event.rawY - lastY).toInt()
                    if (kotlin.math.abs(event.rawX - (lastX)) + kotlin.math.abs(event.rawY - lastY) > d(2f) && downY < 65f) draggingPanel = true
                    if (draggingPanel) {
                        params.x = (params.x - dx).coerceAtLeast(0)
                        params.y = (params.y + dy).coerceAtLeast(0)
                        runCatching { windowManager.updateViewLayout(this, params) }
                    } else if (downX < 68f && downY in 95f..275f) {
                        speedValue = (.5f + ((264f - y) / 160f).coerceIn(0f, 1f) * 2f)
                        getSharedPreferences("keycursor", MODE_PRIVATE).edit().putFloat("speed", speedValue).apply()
                        invalidate()
                    }
                    lastX = event.rawX; lastY = event.rawY
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (!draggingPanel && downY < 60f && downX > width / density - 65f) {
                        typingMode = !typingMode
                        getSharedPreferences("keycursor", MODE_PRIVATE).edit().putBoolean("typing", typingMode).apply()
                    } else if (!draggingPanel && activeControl >= 0) {
                        when (activeControl) {
                            0 -> typingMode = false
                            1 -> dispatchTap()
                            2 -> { dragging = !dragging; }
                        }
                        getSharedPreferences("keycursor", MODE_PRIVATE).edit().putBoolean("typing", typingMode).apply()
                    }
                    activeControl = -1
                    invalidate()
                    return true
                }
            }
            return true
        }
    }
}
