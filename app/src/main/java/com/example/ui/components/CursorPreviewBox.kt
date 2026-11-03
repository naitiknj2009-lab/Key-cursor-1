package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.example.ui.animation.motionPress
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PointerStyle
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmLightPrimary
import com.example.ui.theme.TextWarmLightSecondary
import kotlinx.coroutines.launch

@Composable
fun CursorPreviewBox(
    pointerStyle: PointerStyle,
    cursorSizeDp: Int,
    cursorColor: Long,
    cursorOpacity: Float,
    cursorShadow: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextMuted else TextWarmLightMuted

    val coroutineScope = rememberCoroutineScope()
    val rippleAnim = remember { Animatable(0f) }
    var isSimulatingDrag by remember { mutableStateOf(false) }

    val containerBg = if (isDark) {
        listOf(Color(0x352E231C), Color(0x221B1510))
    } else {
        listOf(Color(0xF0FFFFFF), Color(0xDCF6EFE6))
    }
    val containerBorder = if (isDark) Color(0x28FFFFFF) else Color(0x1F000000)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = containerBg
                )
            )
            .border(1.dp, containerBorder, RoundedCornerShape(26.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Cursor Preview",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x14000000))
                    .border(1.dp, if (isDark) Color(0x28FFFFFF) else Color(0x1A000000), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = pointerStyle.displayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyberCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Canvas Preview Area with Warm Dark Espresso Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF1A130E) else Color(0xFFEDE4D8))
                .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x22000000), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(120.dp)
                    .testTag("cursor_preview_canvas")
            ) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val baseColor = Color(cursorColor)
                val renderColor = baseColor.copy(alpha = cursorOpacity)
                val scale = (cursorSizeDp / 28f)

                // Drag Aura
                if (isSimulatingDrag) {
                    drawCircle(
                        color = Color(0xFFFF9800).copy(alpha = 0.5f),
                        radius = 28.dp.toPx() * scale,
                        center = centerOffset,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // Click Ripple
                if (rippleAnim.value > 0f) {
                    val radius = rippleAnim.value * 35.dp.toPx()
                    val alpha = (1f - rippleAnim.value).coerceIn(0f, 1f)
                    drawCircle(
                        color = baseColor.copy(alpha = alpha * 0.8f),
                        radius = radius,
                        center = centerOffset,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }

                when (pointerStyle) {
                    PointerStyle.CYBER_ARROW -> {
                        val path = Path().apply {
                            val ox = centerOffset.x - 12.dp.toPx() * scale
                            val oy = centerOffset.y - 12.dp.toPx() * scale
                            moveTo(ox, oy)
                            lineTo(ox + 22.dp.toPx() * scale, oy + 16.dp.toPx() * scale)
                            lineTo(ox + 13.dp.toPx() * scale, oy + 16.dp.toPx() * scale)
                            lineTo(ox + 18.dp.toPx() * scale, oy + 26.dp.toPx() * scale)
                            lineTo(ox + 12.dp.toPx() * scale, oy + 28.dp.toPx() * scale)
                            lineTo(ox + 8.dp.toPx() * scale, oy + 17.dp.toPx() * scale)
                            lineTo(ox, oy + 22.dp.toPx() * scale)
                            close()
                        }
                        drawPath(path, renderColor)
                        drawPath(path, Color.White.copy(alpha = cursorOpacity * 0.9f), style = Stroke(width = 1.5.dp.toPx()))
                    }
                    PointerStyle.TRADITIONAL_ARROW -> {
                        val path = Path().apply {
                            val ox = centerOffset.x - 10.dp.toPx() * scale
                            val oy = centerOffset.y - 14.dp.toPx() * scale
                            moveTo(ox, oy)
                            lineTo(ox, oy + 24.dp.toPx() * scale)
                            lineTo(ox + 7.dp.toPx() * scale, oy + 17.dp.toPx() * scale)
                            lineTo(ox + 13.dp.toPx() * scale, oy + 28.dp.toPx() * scale)
                            lineTo(ox + 17.dp.toPx() * scale, oy + 25.dp.toPx() * scale)
                            lineTo(ox + 10.dp.toPx() * scale, oy + 15.dp.toPx() * scale)
                            lineTo(ox + 17.dp.toPx() * scale, oy + 15.dp.toPx() * scale)
                            close()
                        }
                        drawPath(path, renderColor)
                        drawPath(path, Color.Black.copy(alpha = cursorOpacity), style = Stroke(width = 2.dp.toPx()))
                    }
                    PointerStyle.PRECISION_DOT -> {
                        val r = 10.dp.toPx() * scale
                        drawCircle(renderColor, radius = r, center = centerOffset, style = Stroke(width = 2.dp.toPx()))
                        drawCircle(Color.White, radius = r * 0.35f, center = centerOffset)
                    }
                    PointerStyle.CROSSHAIR -> {
                        val r = 12.dp.toPx() * scale
                        val gap = 3.dp.toPx() * scale
                        drawLine(renderColor, Offset(centerOffset.x, centerOffset.y - gap), Offset(centerOffset.x, centerOffset.y - r), strokeWidth = 2.dp.toPx())
                        drawLine(renderColor, Offset(centerOffset.x, centerOffset.y + gap), Offset(centerOffset.x, centerOffset.y + r), strokeWidth = 2.dp.toPx())
                        drawLine(renderColor, Offset(centerOffset.x - gap, centerOffset.y), Offset(centerOffset.x - r, centerOffset.y), strokeWidth = 2.dp.toPx())
                        drawLine(renderColor, Offset(centerOffset.x + gap, centerOffset.y), Offset(centerOffset.x + r, centerOffset.y), strokeWidth = 2.dp.toPx())
                        drawCircle(Color.White, radius = 1.5.dp.toPx(), center = centerOffset)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interaction test buttons for preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        rippleAnim.snapTo(0f)
                        rippleAnim.animateTo(1f, tween(300))
                        rippleAnim.snapTo(0f)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .motionPress()
                    .testTag("preview_click_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = "Simulate Click", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Test Click", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    isSimulatingDrag = !isSimulatingDrag
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .motionPress()
                    .testTag("preview_drag_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isSimulatingDrag) Color(0xFFFF9800) else TextSecondary
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Simulate Drag", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text(if (isSimulatingDrag) "Drop" else "Test Drag", fontSize = 12.sp)
            }
        }
    }
}
