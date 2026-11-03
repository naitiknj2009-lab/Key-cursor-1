package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences
import com.example.data.SpeedPreset
import com.example.service.CursorAccessibilityService
import com.example.ui.animation.motionGlow
import com.example.ui.animation.motionPress
import com.example.ui.theme.AmberTrack
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark
import com.example.ui.theme.NeonLimeGlow
import com.example.ui.theme.ObsidianDeckBg
import com.example.ui.theme.ObsidianDeckTile
import com.example.ui.theme.ObsidianDeckTileActive
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmLightPrimary
import com.example.ui.theme.TextWarmLightSecondary
import com.example.ui.theme.TextWarmMuted
import com.example.ui.theme.TextWarmPrimary
import com.example.ui.theme.TextWarmSecondary
import com.example.ui.theme.WarmAccentAmber
import com.example.ui.theme.WarmEmerald
import kotlin.math.roundToInt

/**
 * Interactive preview and settings for the Floating Control Panel HUD.
 * Styled directly after the user reference image:
 * - Electric lime (#B7F34D) accents
 * - Tactile squircle tiles
 * - Horizontal pill slider with warm amber (#FFA834) glow and floating percentage bubble
 * - Compact Orb node vs Expanded Deck switch
 */
@Composable
fun FloatingPanelDeckPreview(
    prefs: KeyCursorPreferences,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextWarmPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextWarmSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextWarmMuted else TextWarmLightMuted

    val state by CursorStateHolder.state.collectAsState()
    var isExpandedPreview by remember { mutableStateOf(!prefs.isPanelCompact) }
    var panelAlphaVal by remember { mutableFloatStateOf(prefs.panelAlpha) }
    var liveSpeedVal by remember { mutableFloatStateOf(prefs.baseSpeed) }

    GlassCard(
        borderColor = if (prefs.isPanelEnabled) NeonLime.copy(alpha = 0.6f) else Color(0x33FFFFFF),
        glowColor = if (prefs.isPanelEnabled) NeonLimeGlow else Color.Transparent,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Title & Enable Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (prefs.isPanelEnabled) NeonLime else Color(0x28FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = if (prefs.isPanelEnabled) NeonLimeDark else textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tactile Floating HUD Deck",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "System-wide overlay accessible over all Android apps",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }

                Switch(
                    checked = prefs.isPanelEnabled,
                    onCheckedChange = { next ->
                        prefs.isPanelEnabled = next
                        CursorAccessibilityService.instance?.reloadFloatingPanel()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonLimeDark,
                        checkedTrackColor = NeonLime,
                        uncheckedThumbColor = Color(0xFF8A796A),
                        uncheckedTrackColor = Color(0xFF2C221A)
                    ),
                    modifier = Modifier.testTag("deck_master_switch")
                )
            }

            // Mode Selector: Compact Orb Node vs Expanded Deck
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (!isExpandedPreview) NeonLime else Color(0x18FFFFFF))
                        .border(
                            1.dp,
                            if (!isExpandedPreview) NeonLime else Color(0x26FFFFFF),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            isExpandedPreview = false
                            prefs.isPanelCompact = true
                            CursorAccessibilityService.instance?.reloadFloatingPanel()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Compact Orb Node",
                        fontSize = 12.sp,
                        fontWeight = if (!isExpandedPreview) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isExpandedPreview) NeonLimeDark else textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isExpandedPreview) NeonLime else Color(0x18FFFFFF))
                        .border(
                            1.dp,
                            if (isExpandedPreview) NeonLime else Color(0x26FFFFFF),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            isExpandedPreview = true
                            prefs.isPanelCompact = false
                            CursorAccessibilityService.instance?.reloadFloatingPanel()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Expanded Tactical Deck",
                        fontSize = 12.sp,
                        fontWeight = if (isExpandedPreview) FontWeight.Bold else FontWeight.Medium,
                        color = if (isExpandedPreview) NeonLimeDark else textSecondary
                    )
                }
            }

            // Interactive Live Visual Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0B0E12))
                    .border(1.dp, Color(0x33445566), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isExpandedPreview) {
                    // Preview Compact Mode: 56dp Circular Node
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ObsidianDeckBg)
                                .border(2.dp, if (prefs.isCursorEnabled) NeonLime else Color(0x66FFFFFF), CircleShape)
                                .motionPress {
                                    isExpandedPreview = true
                                    prefs.isPanelCompact = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Cursor Orb",
                                tint = if (prefs.isCursorEnabled) NeonLime else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                            // Status Dot
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(if (prefs.isCursorEnabled) NeonLime else AmberTrack)
                                    .border(1.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-4).dp, y = (-4).dp)
                            )
                        }
                        Text(
                            text = "Tap to expand into full control deck",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                } else {
                    // Preview Expanded Deck (Reference UI phone 3 & phone 2 style)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(ObsidianDeckBg.copy(alpha = panelAlphaVal))
                            .border(1.5.dp, Color(0x4D384759), RoundedCornerShape(22.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x5519202A))
                                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (prefs.isCursorEnabled) NeonLime else AmberTrack)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (prefs.isCursorEnabled) "KEYCURSOR ACTIVE" else "PAUSED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (prefs.isCursorEnabled) NeonLime else AmberTrack
                                    )
                                }
                            }

                            // Collapse button
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                                    .clickable { isExpandedPreview = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Collapse",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Row 1: Hero Power Tile (Neon Lime) & Typing Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Power tile
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (prefs.isCursorEnabled) NeonLime else ObsidianDeckTile)
                                    .border(
                                        1.dp,
                                        if (prefs.isCursorEnabled) NeonLime else Color(0x33FFFFFF),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .motionPress {
                                        CursorAccessibilityService.instance?.toggleCursorActive()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PowerSettingsNew,
                                        contentDescription = null,
                                        tint = if (prefs.isCursorEnabled) NeonLimeDark else NeonLime,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (prefs.isCursorEnabled) "MOUSE ON" else "PAUSED",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (prefs.isCursorEnabled) NeonLimeDark else Color.White
                                    )
                                }
                            }

                            // Mode Tile
                            Box(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(ObsidianDeckTile)
                                    .border(
                                        1.dp,
                                        if (state.isTypingMode) WarmEmerald else Color(0x33FFFFFF),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .motionPress {
                                        // toggle mode
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Keyboard,
                                        contentDescription = null,
                                        tint = if (state.isTypingMode) WarmEmerald else textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (state.isTypingMode) "TYPING" else "NAV",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.isTypingMode) WarmEmerald else Color.White
                                    )
                                }
                            }
                        }

                        // Row 2: Tactile Squircle Action Tiles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SquircleActionItem(
                                icon = Icons.Default.CenterFocusStrong,
                                label = "Center",
                                accent = NeonLime,
                                onClick = { CursorAccessibilityService.instance?.recenterCursor() },
                                modifier = Modifier.weight(1f)
                            )
                            SquircleActionItem(
                                icon = Icons.Default.Mouse,
                                label = "L-Click",
                                accent = AmberTrack,
                                onClick = { CursorAccessibilityService.instance?.performFloatingLeftClick() },
                                modifier = Modifier.weight(1f)
                            )
                            SquircleActionItem(
                                icon = Icons.Default.Mouse,
                                label = "R-Click",
                                accent = AmberTrack,
                                onClick = { CursorAccessibilityService.instance?.performFloatingRightClick() },
                                modifier = Modifier.weight(1f)
                            )
                            SquircleActionItem(
                                icon = if (prefs.cursorVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                label = if (prefs.cursorVisibility) "Hide" else "Show",
                                accent = Color(0xFFF472B6),
                                onClick = { CursorAccessibilityService.instance?.toggleVisibility() },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 3: Tactile Horizontal Slider with Floating Percentage Bubble
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Speed Sensitivity", fontSize = 11.sp, color = textSecondary)
                                val mult = "%.1fx".format(liveSpeedVal / 14f)
                                Text("$mult (${liveSpeedVal.toInt()} px/f)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberTrack)
                            }

                            // Tactile Slider with Bubble Canvas
                            val fraction = ((liveSpeedVal - 4f) / (40f - 4f)).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, _ ->
                                            val newFrac = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                                            val newSpd = 4f + newFrac * 36f
                                            liveSpeedVal = newSpd
                                            prefs.baseSpeed = newSpd
                                            CursorAccessibilityService.instance?.setSpeed(newSpd)
                                        }
                                    }
                            ) {
                                Canvas(modifier = Modifier.matchParentSize()) {
                                    val w = size.width
                                    val h = size.height
                                    val trackH = 12.dp.toPx()
                                    val trackY = (h - trackH) / 2f
                                    val pad = 12.dp.toPx()

                                    // Track background
                                    drawRoundRect(
                                        color = Color(0xFF1B232E),
                                        topLeft = Offset(pad, trackY),
                                        size = Size(w - 2 * pad, trackH),
                                        cornerRadius = CornerRadius(trackH / 2f)
                                    )

                                    // Progress fill (Amber glow)
                                    val knobX = pad + fraction * (w - 2 * pad)
                                    drawRoundRect(
                                        color = AmberTrack,
                                        topLeft = Offset(pad, trackY),
                                        size = Size((knobX - pad).coerceAtLeast(0f), trackH),
                                        cornerRadius = CornerRadius(trackH / 2f)
                                    )

                                    // Circular Knob
                                    val knobRadius = 9.dp.toPx()
                                    drawCircle(Color.White, knobRadius, Offset(knobX, h / 2f))
                                    drawCircle(AmberTrack, knobRadius * 0.45f, Offset(knobX, h / 2f))

                                    // Floating bubble (Phone 3 in reference image)
                                    val bubbleW = 34.dp.toPx()
                                    val bubbleH = 16.dp.toPx()
                                    val bubbleTop = trackY - 14.dp.toPx()
                                    val bubbleLeft = (knobX - bubbleW / 2f).coerceIn(2f, w - bubbleW - 2f)

                                    drawRoundRect(
                                        color = Color(0xFF141921),
                                        topLeft = Offset(bubbleLeft, bubbleTop),
                                        size = Size(bubbleW, bubbleH),
                                        cornerRadius = CornerRadius(8.dp.toPx())
                                    )
                                    drawRoundRect(
                                        color = AmberTrack,
                                        topLeft = Offset(bubbleLeft, bubbleTop),
                                        size = Size(bubbleW, bubbleH),
                                        cornerRadius = CornerRadius(8.dp.toPx()),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx())
                                    )
                                }

                                // Bubble Percentage Text placed over the bubble
                                val pct = (fraction * 100).toInt()
                                val knobOffsetFraction = ((fraction * 100f).roundToInt()).toFloat()
                                Text(
                                    text = "$pct%",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset {
                                            val xPx = 12.dp.toPx() + fraction * (1000 - 24.dp.toPx())
                                            IntOffset(0, 2)
                                        }
                                        .padding(start = 8.dp)
                                )
                            }

                            // Quick Preset Pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(
                                    "0.5x" to 7f,
                                    "1.0x" to 14f,
                                    "1.8x" to 25f,
                                    "2.5x" to 35f
                                ).forEach { (label, spd) ->
                                    val isCur = kotlin.math.abs(liveSpeedVal - spd) < 4f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isCur) NeonLime else Color(0x221C2430))
                                            .border(
                                                1.dp,
                                                if (isCur) NeonLime else Color(0x26FFFFFF),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                liveSpeedVal = spd
                                                prefs.baseSpeed = spd
                                                CursorAccessibilityService.instance?.setSpeed(spd)
                                            }
                                            .padding(vertical = 5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCur) NeonLimeDark else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Opacity & Position Reset settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Deck Opacity: ${(panelAlphaVal * 100).toInt()}%", fontSize = 12.sp, color = textSecondary)
                    Slider(
                        value = panelAlphaVal,
                        onValueChange = { newVal ->
                            panelAlphaVal = newVal
                            prefs.panelAlpha = newVal
                            CursorAccessibilityService.instance?.reloadFloatingPanel()
                        },
                        valueRange = 0.4f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonLime,
                            activeTrackColor = NeonLime,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        prefs.panelX = 40
                        prefs.panelY = 180
                        CursorAccessibilityService.instance?.reloadFloatingPanel()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x28FFFFFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset Position", fontSize = 11.sp, color = textPrimary)
                }
            }
        }
    }
}

@Composable
private fun SquircleActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianDeckTile)
            .border(1.dp, Color(0x33445566), RoundedCornerShape(14.dp))
            .motionPress(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
