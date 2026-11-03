package com.example.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

/**
 * Framer Motion-inspired physics and animation specifications for Jetpack Compose.
 * Provides fluid spring physics, interactive press scales, and harmonic ambient glows.
 */
object MotionSprings {
    val Bouncy: AnimationSpec<Float> = spring(
        dampingRatio = 0.65f, // Framer Motion style bouncy spring
        stiffness = Spring.StiffnessMediumLow
    )

    val Snappy: AnimationSpec<Float> = spring(
        dampingRatio = 0.82f,
        stiffness = Spring.StiffnessMedium
    )

    val Smooth: AnimationSpec<Float> = spring(
        dampingRatio = 1.0f, // Critically damped, no overshoot
        stiffness = Spring.StiffnessLow
    )
}

/**
 * Modifier replicating Framer Motion's whileTap={{ scale: 0.96 }} interactive spring physics.
 */
fun Modifier.motionPress(
    targetScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    this
        .scale(scale.value)
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                scope.launch {
                    scale.animateTo(targetScale, MotionSprings.Snappy)
                }
                val up = waitForUpOrCancellation()
                scope.launch {
                    scale.animateTo(1f, MotionSprings.Bouncy)
                }
                if (up != null && onClick != null) {
                    onClick()
                }
            }
        }
}

/**
 * Ambient pulsing glow replicating Framer Motion's animate={{ boxShadow: [...] }}
 */
fun Modifier.motionGlow(
    color: Color = Color(0xFF00E5FF),
    radiusDp: Float = 24f
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "MotionGlow")
    val alpha by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MotionGlowAlpha"
    )

    this.drawBehind {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha),
                    Color.Transparent
                ),
                radius = size.maxDimension * 0.75f
            )
        )
    }
}
