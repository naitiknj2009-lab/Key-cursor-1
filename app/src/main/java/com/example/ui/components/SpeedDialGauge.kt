package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.MotionSprings
import com.example.ui.animation.motionPress
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmLightPrimary
import com.example.ui.theme.TextWarmLightSecondary
import com.example.ui.theme.TextWarmMuted
import com.example.ui.theme.TextWarmPrimary
import com.example.ui.theme.TextWarmSecondary
import com.example.ui.theme.WarmAccentAmber
import com.example.ui.theme.WarmAccentGlow
import com.example.ui.theme.WarmGold

/**
 * Circular Arc Speed Dial Gauge inspired by the thermostat and lighting dials
 * in the reference design. Features smooth arc rendering, tactile center disc,
 * and quick-step (+ / -) adjustment controls.
 */
@Composable
fun SpeedDialGauge(
    currentSpeed: Float,
    speedRange: ClosedFloatingPointRange<Float> = 6f..36f,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val progress = ((currentSpeed - speedRange.start) / (speedRange.endInclusive - speedRange.start)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = MotionSprings.Smooth,
        label = "SpeedGaugeProgress"
    )

    val containerBg = if (isDark) {
        listOf(Color(0x352E231C), Color(0x221B1510))
    } else {
        listOf(Color(0xF0FFFFFF), Color(0xDCF6EFE6))
    }
    val containerBorder = if (isDark) Color(0x28FFFFFF) else Color(0x1F000000)
    val textPrimary = if (isDark) TextWarmPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextWarmSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextWarmMuted else TextWarmLightMuted

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.verticalGradient(colors = containerBg))
            .border(1.dp, containerBorder, RoundedCornerShape(26.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Motion Velocity Dial",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "Precision Pointer Speed Gauge",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x14000000))
                    .border(1.dp, if (isDark) Color(0x28FFFFFF) else Color(0x1A000000), RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${currentSpeed.toInt()} px/tick",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmAccentAmber
                )
            }
        }

        // Circular Gauge Canvas with Center Disc
        Box(
            modifier = Modifier
                .size(200.dp)
                .testTag("speed_dial_gauge_box"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(190.dp)) {
                val strokeWidth = 14.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                val arcSize = Size(diameter, diameter)

                // Background track arc (240 degrees total sweep, starting at 150 degrees)
                drawArc(
                    color = if (isDark) Color(0x2AFFFFFF) else Color(0x18000000),
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Animated Active Gradient Arc
                val activeSweep = animatedProgress * 240f
                if (activeSweep > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                WarmGold,
                                WarmAccentAmber,
                                WarmAccentGlow
                            )
                        ),
                        startAngle = 150f,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // Tactile Center Dial Disc (Reference Style)
            Box(
                modifier = Modifier
                    .size(126.dp)
                    .shadow(16.dp, CircleShape, ambientColor = WarmAccentGlow.copy(alpha = 0.3f))
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFBF8F4),
                                Color(0xFFEDE4D8)
                            )
                        )
                    )
                    .border(2.dp, if (isDark) Color(0x66FFFFFF) else Color(0x33000000), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SPEED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8A7969),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${currentSpeed.toInt()}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F1812)
                    )
                    Text(
                        text = when {
                            currentSpeed < 12f -> "Slow"
                            currentSpeed > 24f -> "Fast"
                            else -> "Normal"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WarmAccentAmber
                    )
                }
            }
        }

        // Stepper Controls: Minus & Plus (Inspired by reference UI)
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minus Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF281F18) else Color(0xFFEDE4D8))
                    .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x28000000), CircleShape)
                    .motionPress(onClick = {
                        val next = (currentSpeed - 2f).coerceIn(speedRange.start, speedRange.endInclusive)
                        onSpeedChange(next)
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease Speed",
                    tint = textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Adjust Velocity",
                fontSize = 12.sp,
                color = textMuted,
                fontWeight = FontWeight.Medium
            )

            // Plus Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF281F18) else Color(0xFFEDE4D8))
                    .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x28000000), CircleShape)
                    .motionPress(onClick = {
                        val next = (currentSpeed + 2f).coerceIn(speedRange.start, speedRange.endInclusive)
                        onSpeedChange(next)
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase Speed",
                    tint = textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
