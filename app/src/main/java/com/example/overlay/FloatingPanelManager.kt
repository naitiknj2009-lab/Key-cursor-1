package com.example.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.MainActivity
import com.example.R
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences
import com.example.data.SpeedPreset
import kotlin.math.abs

/**
 * Redesigned Floating Control Panel overlay inspired by modern tactile glassmorphism.
 * Features:
 * - Tactile circular node in compact mode with neon lime indicator
 * - Dark obsidian frosted deck in expanded mode
 * - Vibrant electric lime (#B7F34D) hero power tile
 * - Quick Left Click and Right Click hardware-bypass buttons
 * - Instant Screen Recenter crosshair tile
 * - Interactive horizontal pill slider with warm amber (#FFA834) glow and floating bubble
 * - Quick speed preset pills (0.5x, 1.0x, 1.8x, 2.5x)
 * - Optimized for low-end tablets (Lenovo Tab M10 / Android 10) with zero overhead.
 */
class FloatingPanelManager(
    private val context: Context,
    private val prefs: KeyCursorPreferences,
    private val onToggleCursor: () -> Unit,
    private val onToggleTypingMode: () -> Unit,
    private val onToggleVisibility: () -> Unit,
    private val onCycleSpeed: () -> Unit,
    private val onRecenterCursor: () -> Unit,
    private val onLeftClick: () -> Unit,
    private val onRightClick: () -> Unit,
    private val onSpeedChanged: (Float) -> Unit
) {
    companion object {
        const val COLOR_OBSIDIAN_BG = 0xFF11151B.toInt()
        const val COLOR_SURFACE_TILE = 0xFF19202A.toInt()
        const val COLOR_SURFACE_TILE_ACTIVE = 0xFF222C3A.toInt()
        const val COLOR_BORDER_DEFAULT = 0x33445566.toInt()
        const val COLOR_NEON_LIME = 0xFFB7F34D.toInt()
        const val COLOR_NEON_LIME_DARK = 0xFF101705.toInt()
        const val COLOR_WARM_AMBER = 0xFFFFA834.toInt()
        const val COLOR_EMERALD = 0xFF34D399.toInt()
    }

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var rootView: FrameLayout? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var isAttached = false
    private var isExpanded = !prefs.isPanelCompact

    @SuppressLint("ClickableViewAccessibility")
    fun show() {
        if (isAttached || !prefs.isPanelEnabled) return

        val frame = FrameLayout(context)
        rootView = frame

        val windowType = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = prefs.panelX
            y = prefs.panelY
        }

        layoutParams = params
        renderPanelContent(frame)

        // Drag handler for the floating window
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = true

        frame.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (abs(dx) > 12 || abs(dy) > 12) {
                        isClick = false
                    }
                    params.x = initialX + dx
                    params.y = initialY + dy
                    try {
                        windowManager.updateViewLayout(frame, params)
                    } catch (e: Exception) {
                        // ignore layout update race conditions
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick && !isExpanded) {
                        isExpanded = true
                        prefs.isPanelCompact = false
                        renderPanelContent(frame)
                    } else {
                        prefs.panelX = params.x
                        prefs.panelY = params.y
                    }
                    true
                }
                else -> false
            }
        }

        try {
            windowManager.addView(frame, params)
            isAttached = true
        } catch (e: Exception) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    windowManager.addView(frame, params)
                    isAttached = true
                }
            } catch (fallbackError: Exception) {
                fallbackError.printStackTrace()
            }
        }
    }

    private fun renderPanelContent(frame: FrameLayout) {
        frame.removeAllViews()
        val density = context.resources.displayMetrics.density

        if (!isExpanded) {
            renderCompactOrb(frame, density)
        } else {
            renderExpandedDeck(frame, density)
        }
    }

    /**
     * Compact Mode: Luminous Circular Node inspired by the reference image's circular moments nodes.
     */
    private fun renderCompactOrb(frame: FrameLayout, density: Float) {
        val orbSize = (56 * density).toInt()

        val orbContainer = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(orbSize, orbSize)

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                val alpha = (prefs.panelAlpha * 255).toInt()
                setColor(Color.argb(alpha, 18, 22, 28))
                val borderColor = if (prefs.isCursorEnabled) COLOR_NEON_LIME else COLOR_BORDER_DEFAULT
                setStroke((2f * density).toInt(), borderColor)
            }
        }

        // Inner glowing ring or accent
        val cursorIcon = ImageView(context).apply {
            setImageResource(R.drawable.ic_fp_cursor)
            val iconColor = if (prefs.isCursorEnabled) COLOR_NEON_LIME else Color.parseColor("#94A3B8")
            setColorFilter(iconColor)
            val iconSize = (24 * density).toInt()
            layoutParams = FrameLayout.LayoutParams(iconSize, iconSize, Gravity.CENTER)
        }
        orbContainer.addView(cursorIcon)

        // Micro status dot (Lime when active, Emerald in typing mode, Warm Amber paused)
        val statusDot = View(context).apply {
            val dotSize = (8 * density).toInt()
            layoutParams = FrameLayout.LayoutParams(dotSize, dotSize).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(0, 0, (8 * density).toInt(), (8 * density).toInt())
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                val isTyping = CursorStateHolder.state.value.isTypingMode
                val dotColor = if (!prefs.isCursorEnabled) COLOR_WARM_AMBER else if (isTyping) COLOR_EMERALD else COLOR_NEON_LIME
                setColor(dotColor)
                setStroke((1 * density).toInt(), Color.WHITE)
            }
        }
        orbContainer.addView(statusDot)

        frame.addView(orbContainer)
    }

    /**
     * Expanded Mode: Tactile Glassmorphic Deck with Neon Lime & Warm Amber accents.
     */
    private fun renderExpandedDeck(frame: FrameLayout, density: Float) {
        val deckWidth = (280 * density).toInt()

        val deck = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(deckWidth, FrameLayout.LayoutParams.WRAP_CONTENT)
            val pad = (14 * density).toInt()
            setPadding(pad, (10 * density).toInt(), pad, (14 * density).toInt())

            background = GradientDrawable().apply {
                cornerRadius = 24 * density
                val alpha = (prefs.panelAlpha * 255).toInt().coerceIn(120, 255)
                setColor(Color.argb(alpha, 17, 21, 27))
                setStroke((1.5f * density).toInt(), Color.argb(140, 52, 64, 82))
            }
        }

        // 1. Top Drag Handle & Deck Header
        val header = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (10 * density).toInt())
            }
        }

        // Status indicator pill
        val statusPill = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding((8 * density).toInt(), (3 * density).toInt(), (10 * density).toInt(), (3 * density).toInt())
            background = GradientDrawable().apply {
                cornerRadius = 12 * density
                setColor(Color.argb(120, 25, 32, 42))
                setStroke((1 * density).toInt(), Color.argb(80, 255, 255, 255))
            }
        }

        val dot = View(context).apply {
            val dSize = (6 * density).toInt()
            layoutParams = LinearLayout.LayoutParams(dSize, dSize).apply {
                setMargins(0, 0, (6 * density).toInt(), 0)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(if (prefs.isCursorEnabled) COLOR_NEON_LIME else COLOR_WARM_AMBER)
            }
        }
        val statusText = TextView(context).apply {
            text = if (prefs.isCursorEnabled) "KEYCURSOR ACTIVE" else "KEYCURSOR PAUSED"
            textSize = 10f
            setTextColor(if (prefs.isCursorEnabled) COLOR_NEON_LIME else Color.parseColor("#E2E8F0"))
            paint.isFakeBoldText = true
        }
        statusPill.addView(dot)
        statusPill.addView(statusText)
        header.addView(statusPill)

        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, 1, 1f)
        }
        header.addView(spacer)

        // Minimize / Collapse Button (Tactile circular pill)
        val btnCollapse = TextView(context).apply {
            text = "–"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            val btnSize = (28 * density).toInt()
            layoutParams = LinearLayout.LayoutParams(btnSize, btnSize).apply {
                setMargins(0, 0, (4 * density).toInt(), 0)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.argb(140, 34, 44, 58))
                setStroke((1 * density).toInt(), Color.argb(80, 255, 255, 255))
            }
            setOnClickListener {
                isExpanded = false
                prefs.isPanelCompact = true
                renderPanelContent(frame)
            }
        }
        header.addView(btnCollapse)

        deck.addView(header)

        // 2. Row 1: Hero Power & Mode Switch (Squircle Tiles)
        val row1 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (8 * density).toInt())
            }
        }

        // Hero Power Button (Electric Lime Accent when active)
        val heroPower = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, (48 * density).toInt(), 1.4f).apply {
                setMargins(0, 0, (6 * density).toInt(), 0)
            }
            val active = prefs.isCursorEnabled
            background = GradientDrawable().apply {
                cornerRadius = 16 * density
                if (active) {
                    setColor(COLOR_NEON_LIME)
                } else {
                    setColor(COLOR_SURFACE_TILE)
                    setStroke((1.5f * density).toInt(), Color.argb(100, 255, 255, 255))
                }
            }
            setOnClickListener {
                onToggleCursor()
                renderPanelContent(frame)
            }
        }

        val powerIcon = ImageView(context).apply {
            setImageResource(R.drawable.ic_fp_power)
            val iconTint = if (prefs.isCursorEnabled) COLOR_NEON_LIME_DARK else COLOR_NEON_LIME
            setColorFilter(iconTint)
            val pSize = (20 * density).toInt()
            layoutParams = LinearLayout.LayoutParams(pSize, pSize).apply {
                setMargins(0, 0, (6 * density).toInt(), 0)
            }
        }
        val powerLabel = TextView(context).apply {
            text = if (prefs.isCursorEnabled) "MOUSE ON" else "PAUSED"
            textSize = 12f
            paint.isFakeBoldText = true
            setTextColor(if (prefs.isCursorEnabled) COLOR_NEON_LIME_DARK else Color.WHITE)
        }
        heroPower.addView(powerIcon)
        heroPower.addView(powerLabel)
        row1.addView(heroPower)

        // Mode Tile: Cursor vs Typing Mode
        val isTyping = CursorStateHolder.state.value.isTypingMode
        val modeTile = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, (48 * density).toInt(), 1.2f)
            background = GradientDrawable().apply {
                cornerRadius = 16 * density
                setColor(COLOR_SURFACE_TILE)
                setStroke((1.2f * density).toInt(), if (isTyping) COLOR_EMERALD else Color.argb(80, 255, 255, 255))
            }
            setOnClickListener {
                onToggleTypingMode()
                renderPanelContent(frame)
            }
        }

        val modeIcon = ImageView(context).apply {
            setImageResource(R.drawable.ic_fp_keyboard)
            setColorFilter(if (isTyping) COLOR_EMERALD else Color.parseColor("#94A3B8"))
            val kSize = (18 * density).toInt()
            layoutParams = LinearLayout.LayoutParams(kSize, kSize).apply {
                setMargins(0, 0, (5 * density).toInt(), 0)
            }
        }
        val modeLabel = TextView(context).apply {
            text = if (isTyping) "TYPING" else "NAV"
            textSize = 11f
            paint.isFakeBoldText = true
            setTextColor(if (isTyping) COLOR_EMERALD else Color.WHITE)
        }
        modeTile.addView(modeIcon)
        modeTile.addView(modeLabel)
        row1.addView(modeTile)

        deck.addView(row1)

        // 3. Row 2: Tactile Action Tiles: Recenter, Left Click, Right Click, Visibility, App
        val row2 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (10 * density).toInt())
            }
        }

        // Recenter Button
        val tileRecenter = createSquircleActionTile(
            density = density,
            iconRes = R.drawable.ic_fp_recenter,
            label = "Center",
            accentColor = COLOR_NEON_LIME
        ) {
            onRecenterCursor()
        }
        row2.addView(tileRecenter)

        // Left Click Button
        val tileLeftClick = createSquircleActionTile(
            density = density,
            iconRes = R.drawable.ic_fp_mouse_left,
            label = "L-Click",
            accentColor = COLOR_WARM_AMBER
        ) {
            onLeftClick()
        }
        row2.addView(tileLeftClick)

        // Right Click Button
        val tileRightClick = createSquircleActionTile(
            density = density,
            iconRes = R.drawable.ic_fp_mouse_right,
            label = "R-Click",
            accentColor = COLOR_WARM_AMBER
        ) {
            onRightClick()
        }
        row2.addView(tileRightClick)

        // Eye Visibility Button
        val tileEye = createSquircleActionTile(
            density = density,
            iconRes = R.drawable.ic_fp_eye,
            label = if (prefs.cursorVisibility) "Hide" else "Show",
            accentColor = if (prefs.cursorVisibility) Color.parseColor("#F472B6") else Color.parseColor("#94A3B8")
        ) {
            onToggleVisibility()
            renderPanelContent(frame)
        }
        row2.addView(tileEye)

        // App Launch Button
        val tileApp = createSquircleActionTile(
            density = density,
            iconRes = R.drawable.ic_fp_app,
            label = "App",
            accentColor = Color.parseColor("#38BDF8")
        ) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(intent)
        }
        row2.addView(tileApp)

        deck.addView(row2)

        // 4. Tactile Horizontal Speed Slider with Floating Indicator (Phone 3 in reference image)
        val sliderHeader = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins((2 * density).toInt(), 0, (2 * density).toInt(), (4 * density).toInt())
            }
        }
        val speedTitle = TextView(context).apply {
            text = "Speed Sensitivity"
            textSize = 11f
            setTextColor(Color.parseColor("#94A3B8"))
        }
        val speedValue = TextView(context).apply {
            val mult = "%.1fx".format(prefs.baseSpeed / 14f)
            text = "$mult (${prefs.baseSpeed.toInt()} px/f)"
            textSize = 11f
            setTextColor(COLOR_WARM_AMBER)
            paint.isFakeBoldText = true
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        sliderHeader.addView(speedTitle)
        sliderHeader.addView(speedValue)
        deck.addView(sliderHeader)

        // Custom Tactile Slider View
        val tactileSlider = TactileSliderView(
            context = context,
            currentValue = prefs.baseSpeed,
            minValue = 4f,
            maxValue = 40f,
            density = density,
            onValueChanged = { newSpeed ->
                onSpeedChanged(newSpeed)
                val mult = "%.1fx".format(newSpeed / 14f)
                speedValue.text = "$mult (${newSpeed.toInt()} px/f)"
            }
        ).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (44 * density).toInt()
            )
        }
        deck.addView(tactileSlider)

        // 5. Quick Preset Pills (0.5x, 1.0x, 1.8x, 2.5x)
        val presetRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, (6 * density).toInt(), 0, 0)
            }
        }

        val presets = listOf(
            Triple("0.5x", 7f, SpeedPreset.SLOW),
            Triple("1.0x", 14f, SpeedPreset.NORMAL),
            Triple("1.8x", 25f, SpeedPreset.FAST),
            Triple("2.5x", 35f, SpeedPreset.FAST)
        )

        presets.forEach { (label, speedVal, presetEnum) ->
            val isCurrent = abs(prefs.baseSpeed - speedVal) < 4f
            val pill = TextView(context).apply {
                text = label
                textSize = 10f
                paint.isFakeBoldText = isCurrent
                setTextColor(if (isCurrent) COLOR_NEON_LIME_DARK else Color.WHITE)
                gravity = Gravity.CENTER
                val padH = (10 * density).toInt()
                val padV = (5 * density).toInt()
                setPadding(padH, padV, padH, padV)

                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    val m = (2 * density).toInt()
                    setMargins(m, 0, m, 0)
                }
                layoutParams = lp

                background = GradientDrawable().apply {
                    cornerRadius = 12 * density
                    if (isCurrent) {
                        setColor(COLOR_NEON_LIME)
                    } else {
                        setColor(Color.argb(110, 28, 36, 48))
                        setStroke((1 * density).toInt(), Color.argb(60, 255, 255, 255))
                    }
                }

                setOnClickListener {
                    prefs.baseSpeed = speedVal
                    prefs.speedPreset = presetEnum
                    onSpeedChanged(speedVal)
                    renderPanelContent(frame)
                }
            }
            presetRow.addView(pill)
        }
        deck.addView(presetRow)

        frame.addView(deck)
    }

    private fun createSquircleActionTile(
        density: Float,
        iconRes: Int,
        label: String,
        accentColor: Int,
        onClick: () -> Unit
    ): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(0, (52 * density).toInt(), 1f).apply {
                val m = (2 * density).toInt()
                setMargins(m, 0, m, 0)
            }
            layoutParams = lp

            background = GradientDrawable().apply {
                cornerRadius = 14 * density
                setColor(COLOR_SURFACE_TILE)
                setStroke((1 * density).toInt(), Color.argb(70, 255, 255, 255))
            }

            val icon = ImageView(context).apply {
                setImageResource(iconRes)
                setColorFilter(accentColor)
                val iSize = (18 * density).toInt()
                layoutParams = LinearLayout.LayoutParams(iSize, iSize)
            }

            val text = TextView(context).apply {
                this.text = label
                textSize = 9.5f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                paint.isFakeBoldText = true
                val topMargin = (2 * density).toInt()
                setPadding(0, topMargin, 0, 0)
            }

            addView(icon)
            addView(text)

            setOnClickListener { onClick() }
        }
    }

    fun hide() {
        if (!isAttached) return
        try {
            rootView?.let { windowManager.removeView(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            rootView = null
            layoutParams = null
            isAttached = false
        }
    }
}

/**
 * Interactive tactile slider view replicating Phone 3 in the user reference image.
 * Features:
 * - Rounded pill track
 * - Warm amber illumination (#FFA834)
 * - Glowing circular knob
 * - Live floating percentage/speed bubble
 */
@SuppressLint("ViewConstructor")
private class TactileSliderView(
    context: Context,
    var currentValue: Float,
    val minValue: Float,
    val maxValue: Float,
    val density: Float,
    val onValueChanged: (Float) -> Unit
) : View(context) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1B232E")
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = FloatingPanelManager.COLOR_WARM_AMBER
        style = Paint.Style.FILL
    }

    private val knobOuterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val knobInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = FloatingPanelManager.COLOR_WARM_AMBER
        style = Paint.Style.FILL
    }

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#141921")
        style = Paint.Style.FILL
    }

    private val bubbleBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = FloatingPanelManager.COLOR_WARM_AMBER
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
    }

    private val bubbleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 9.5f * density
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val trackRect = RectF()
    private val progressRect = RectF()
    private val bubbleRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val trackHeight = 12 * density
        val trackTop = (h - trackHeight) / 2f
        val trackBottom = trackTop + trackHeight

        val pad = 16 * density
        trackRect.set(pad, trackTop, w - pad, trackBottom)
        canvas.drawRoundRect(trackRect, trackHeight / 2f, trackHeight / 2f, trackPaint)

        // Progress calculation
        val fraction = ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
        val knobX = pad + fraction * (w - 2 * pad)
        val knobY = h / 2f

        progressRect.set(pad, trackTop, knobX, trackBottom)
        canvas.drawRoundRect(progressRect, trackHeight / 2f, trackHeight / 2f, progressPaint)

        // Glowing Knob (Reference: White circle with warm amber center)
        val knobRadius = 9 * density
        canvas.drawCircle(knobX, knobY, knobRadius, knobOuterPaint)
        canvas.drawCircle(knobX, knobY, knobRadius * 0.45f, knobInnerPaint)

        // Floating percentage/multiplier bubble (Direct reference to Phone 3 "40%" bubble)
        val percent = (fraction * 100).toInt()
        val bubbleText = "$percent%"
        val bubbleWidth = 32 * density
        val bubbleHeight = 16 * density
        val bubbleY = trackTop - (10 * density)

        val bLeft = (knobX - bubbleWidth / 2f).coerceIn(4f, w - bubbleWidth - 4f)
        val bTop = bubbleY - bubbleHeight / 2f
        bubbleRect.set(bLeft, bTop, bLeft + bubbleWidth, bTop + bubbleHeight)

        canvas.drawRoundRect(bubbleRect, 8 * density, 8 * density, bubblePaint)
        canvas.drawRoundRect(bubbleRect, 8 * density, 8 * density, bubbleBorderPaint)
        canvas.drawText(bubbleText, bubbleRect.centerX(), bubbleRect.centerY() + 3.5f * density, bubbleTextPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val pad = 16 * density
                val w = width.toFloat()
                val fraction = ((event.x - pad) / (w - 2 * pad)).coerceIn(0f, 1f)
                currentValue = minValue + fraction * (maxValue - minValue)
                onValueChanged(currentValue)
                invalidate()
                parent?.requestDisallowInterceptTouchEvent(true)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
