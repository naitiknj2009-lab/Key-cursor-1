package com.example.overlay

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.example.data.KeyCursorPreferences
import com.example.data.PointerStyle

/**
 * High-performance hardware-accelerated View for rendering custom cursor styles,
 * specular rims, drop shadows, click ripples, and drag aura effects.
 */
class CursorView(
    context: Context,
    private val prefs: KeyCursorPreferences
) : View(context) {

    private val cursorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }

    private val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val dragAuraPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val path = Path()

    // Animation states
    private var rippleRadius = 0f
    private var rippleAlpha = 0
    private var isDragging = false
    private var dragPulsePhase = 0f

    private var rippleAnimator: ValueAnimator? = null
    private var dragPulseAnimator: ValueAnimator? = null

    init {
        setWillNotDraw(false)
    }

    fun triggerClickAnimation() {
        if (!prefs.clickAnimation) return
        rippleAnimator?.cancel()
        rippleAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 240L
            interpolator = DecelerateInterpolator()
            addUpdateListener { anim ->
                val progress = anim.animatedValue as Float
                rippleRadius = progress * 40f
                rippleAlpha = ((1f - progress) * 200).toInt()
                invalidate()
            }
            start()
        }
    }

    fun setDraggingState(dragging: Boolean) {
        if (isDragging == dragging) return
        isDragging = dragging
        if (dragging && prefs.dragAnimation) {
            dragPulseAnimator?.cancel()
            dragPulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 800L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                addUpdateListener { anim ->
                    dragPulsePhase = anim.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            dragPulseAnimator?.cancel()
            dragPulseAnimator = null
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!prefs.cursorVisibility) return

        val sizePx = prefs.cursorSizeDp * resources.displayMetrics.density
        val baseColor = prefs.cursorColor.toInt()
        val opacity = prefs.cursorOpacity

        cursorPaint.color = baseColor
        cursorPaint.alpha = (opacity * 255).toInt()

        if (prefs.cursorShadow) {
            cursorPaint.setShadowLayer(8f, 2f, 4f, Color.argb(120, 0, 0, 0))
            strokePaint.setShadowLayer(8f, 2f, 4f, Color.argb(120, 0, 0, 0))
        } else {
            cursorPaint.clearShadowLayer()
            strokePaint.clearShadowLayer()
        }

        // Draw Click Ripple if active
        if (rippleRadius > 0f && rippleAlpha > 0) {
            ripplePaint.color = baseColor
            ripplePaint.alpha = rippleAlpha
            canvas.drawCircle(0f, 0f, rippleRadius, ripplePaint)
        }

        // Draw Drag Aura if active
        if (isDragging && prefs.dragAnimation) {
            dragAuraPaint.color = Color.parseColor("#FF9800") // Amber drag glow
            dragAuraPaint.alpha = (100 + (dragPulsePhase * 120)).toInt()
            val auraRadius = (sizePx * 0.7f) + (dragPulsePhase * 8f)
            canvas.drawCircle(sizePx * 0.4f, sizePx * 0.4f, auraRadius, dragAuraPaint)
        }

        when (prefs.pointerStyle) {
            PointerStyle.CYBER_ARROW -> drawCyberArrow(canvas, sizePx, baseColor)
            PointerStyle.TRADITIONAL_ARROW -> drawTraditionalArrow(canvas, sizePx, baseColor)
            PointerStyle.PRECISION_DOT -> drawPrecisionDot(canvas, sizePx, baseColor)
            PointerStyle.CROSSHAIR -> drawCrosshair(canvas, sizePx, baseColor)
        }
    }

    private fun drawCyberArrow(canvas: Canvas, size: Float, baseColor: Int) {
        path.reset()
        // Sleek sharp faceted cyber cursor
        path.moveTo(0f, 0f)
        path.lineTo(size * 0.95f, size * 0.65f)
        path.lineTo(size * 0.55f, size * 0.65f)
        path.lineTo(size * 0.75f, size * 1.05f)
        path.lineTo(size * 0.52f, size * 1.15f)
        path.lineTo(size * 0.35f, size * 0.72f)
        path.lineTo(0f, size * 0.92f)
        path.close()

        canvas.drawPath(path, cursorPaint)

        // Luminous specular edge
        strokePaint.color = Color.WHITE
        strokePaint.alpha = (cursorPaint.alpha * 0.85f).toInt()
        strokePaint.strokeWidth = 2f
        canvas.drawPath(path, strokePaint)
    }

    private fun drawTraditionalArrow(canvas: Canvas, size: Float, baseColor: Int) {
        path.reset()
        path.moveTo(0f, 0f)
        path.lineTo(0f, size)
        path.lineTo(size * 0.28f, size * 0.72f)
        path.lineTo(size * 0.55f, size * 1.15f)
        path.lineTo(size * 0.70f, size * 1.05f)
        path.lineTo(size * 0.42f, size * 0.62f)
        path.lineTo(size * 0.72f, size * 0.62f)
        path.close()

        canvas.drawPath(path, cursorPaint)

        // Classic dark outline
        strokePaint.color = Color.BLACK
        strokePaint.alpha = cursorPaint.alpha
        strokePaint.strokeWidth = 2.5f
        canvas.drawPath(path, strokePaint)
    }

    private fun drawPrecisionDot(canvas: Canvas, size: Float, baseColor: Int) {
        val radius = size * 0.32f
        val center = size * 0.5f

        // Outer reticle ring
        strokePaint.color = baseColor
        strokePaint.alpha = cursorPaint.alpha
        strokePaint.strokeWidth = 2.5f
        canvas.drawCircle(center, center, radius, strokePaint)

        // Center dot
        cursorPaint.color = Color.WHITE
        canvas.drawCircle(center, center, radius * 0.35f, cursorPaint)
    }

    private fun drawCrosshair(canvas: Canvas, size: Float, baseColor: Int) {
        val center = size * 0.5f
        val gap = size * 0.12f
        val extent = size * 0.45f

        strokePaint.color = baseColor
        strokePaint.alpha = cursorPaint.alpha
        strokePaint.strokeWidth = 2.5f

        // Top line
        canvas.drawLine(center, center - gap, center, center - extent, strokePaint)
        // Bottom line
        canvas.drawLine(center, center + gap, center, center + extent, strokePaint)
        // Left line
        canvas.drawLine(center - gap, center, center - extent, center, strokePaint)
        // Right line
        canvas.drawLine(center + gap, center, center + extent, center, strokePaint)

        // Center dot
        cursorPaint.color = Color.WHITE
        canvas.drawCircle(center, center, 2.5f, cursorPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        rippleAnimator?.cancel()
        dragPulseAnimator?.cancel()
    }
}
