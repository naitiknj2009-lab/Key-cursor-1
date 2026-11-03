package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences
import com.example.data.SpeedPreset
import com.example.service.CursorAccessibilityService
import com.example.ui.animation.motionGlow
import com.example.ui.animation.motionPress
import com.example.ui.components.CursorPreviewBox
import com.example.ui.components.FloatingPanelDeckPreview
import com.example.ui.components.GlassCard
import com.example.ui.components.SpeedDialGauge
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmLightPrimary
import com.example.ui.theme.TextWarmLightSecondary
import com.example.ui.theme.TextWarmMuted
import com.example.ui.theme.TextWarmPrimary
import com.example.ui.theme.TextWarmSecondary
import com.example.ui.theme.WarmAccentAmber
import com.example.ui.theme.WarmAccentGlow
import com.example.ui.theme.WarmEmerald
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmPillActive
import com.example.ui.theme.WarmPillActiveLight
import com.example.ui.theme.WarmPillActiveLightText
import com.example.ui.theme.WarmPillActiveText
import com.example.ui.theme.WarmRose
import com.example.ui.theme.WarmTerracotta

@Composable
fun DashboardScreen(
    prefs: KeyCursorPreferences,
    onNavigateToSettings: () -> Unit,
    onNavigateToTestPad: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onToggleTheme: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextWarmPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextWarmSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextWarmMuted else TextWarmLightMuted

    val context = LocalContext.current
    val state by CursorStateHolder.state.collectAsState()
    val scrollState = rememberScrollState()
    val filterScrollState = rememberScrollState()
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    val filterOptions = listOf("All Controls", "Mouse Mode", "Speed Dial", "Hardware Keys", "HUD Overlay")
    val isAccessibilityEnabled = state.isServiceRunning

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Header inspired by Reference UI ("Hi Robbie" / "Hi Sarah" header layout)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF2B2018) else Color(0xFFEDE4D8))
                        .border(1.5.dp, if (isDark) Color(0x44FFFFFF) else Color(0x22000000), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_keycursor_logo),
                        contentDescription = "KeyCursor Emblem",
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "KeyCursor",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Hardware Keyboard Controller",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }
            }

            // Right side: Quick Theme Toggle & Status Capsule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Theme Toggle Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0x28FFFFFF) else Color(0x14000000))
                        .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x20000000), CircleShape)
                        .motionPress(onClick = { onToggleTheme?.invoke() })
                        .testTag("theme_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = if (isDark) WarmAccentAmber else Color(0xFFE28743),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Quick Status Capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0x28FFFFFF) else Color(0x14000000))
                        .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x20000000), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (state.isCursorActive && isAccessibilityEnabled) WarmEmerald else WarmRose)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!isAccessibilityEnabled) "Service Off" else if (state.isCursorActive) "Active" else "Paused",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isCursorActive && isAccessibilityEnabled) WarmEmerald else WarmRose
                        )
                    }
                }
            }
        }

        // Horizontal Category Filter Pills (Reference Style: High Contrast Pill when Active)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(filterScrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filterOptions.forEachIndexed { index, option ->
                val isSelected = selectedFilterIndex == index
                val pillBg = if (isSelected) {
                    if (isDark) WarmPillActive else WarmPillActiveLight
                } else {
                    if (isDark) Color(0x24FFFFFF) else Color(0x12000000)
                }
                val pillBorder = if (isSelected) {
                    if (isDark) Color.White else Color(0xFF1A130D)
                } else {
                    if (isDark) Color(0x1AFFFFFF) else Color(0x1A000000)
                }
                val pillTextColor = if (isSelected) {
                    if (isDark) WarmPillActiveText else WarmPillActiveLightText
                } else {
                    textSecondary
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillBg)
                        .border(1.dp, pillBorder, RoundedCornerShape(20.dp))
                        .motionPress(onClick = { selectedFilterIndex = index })
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = pillTextColor
                    )
                }
            }
        }

        // Accessibility Permission Notice (Warm amber warning card if service not yet enabled)
        if (!isAccessibilityEnabled) {
            GlassCard(
                borderColor = WarmAccentAmber,
                glowColor = WarmAccentGlow.copy(alpha = 0.2f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = WarmAccentGlow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Accessibility Service Required",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                    Text(
                        text = "Android requires granting Accessibility permission so KeyCursor can intercept arrow keys and dispatch touch gestures.",
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .motionPress()
                            .testTag("enable_accessibility_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAccentAmber),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Enable KeyCursor in Settings", color = Color(0xFF160F0A), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Bento Card 1: Master Hero Status & Switch (Reference Card Style)
        GlassCard(
            borderColor = if (state.isCursorActive) WarmAccentAmber else Color(0x33FFFFFF),
            glowColor = if (state.isCursorActive) WarmAccentGlow.copy(alpha = 0.2f) else Color.Transparent,
            modifier = if (state.isCursorActive) Modifier.motionGlow(WarmAccentAmber, 32f) else Modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Master Mouse Control",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = if (state.isTypingMode) "Typing Mode (Keys pass through)" else "Cursor Mode (Arrow keys navigate mouse)",
                            fontSize = 12.sp,
                            color = if (state.isTypingMode) WarmEmerald else WarmAccentAmber
                        )
                    }

                    // Tactile Warm Switch (Reference Style)
                    Switch(
                        checked = state.isCursorActive && isAccessibilityEnabled,
                        onCheckedChange = {
                            if (isAccessibilityEnabled) {
                                CursorAccessibilityService.instance?.toggleCursorActive()
                            } else {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF1E140E),
                            checkedTrackColor = WarmAccentAmber,
                            uncheckedThumbColor = Color(0xFF8A796A),
                            uncheckedTrackColor = Color(0xFF2C221A)
                        )
                    )
                }

                // Master Start / Pause Button
                Button(
                    onClick = {
                        if (isAccessibilityEnabled) {
                            CursorAccessibilityService.instance?.toggleCursorActive()
                        } else {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .motionPress()
                        .testTag("main_toggle_cursor_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isCursorActive && isAccessibilityEnabled) {
                            Color(0xFFFBF8F4)
                        } else {
                            WarmAccentAmber
                        }
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = if (state.isCursorActive && isAccessibilityEnabled) Icons.Default.PowerSettingsNew else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF18120E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!isAccessibilityEnabled) {
                            "Setup & Activate KeyCursor"
                        } else if (state.isCursorActive) {
                            "Pause Mouse Control"
                        } else {
                            "Resume Mouse Control"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF18120E)
                    )
                }

                // Shortcut Hint
                Text(
                    text = "Shortcut: Press Alt + C anytime to toggle typing vs cursor mode.",
                    fontSize = 11.sp,
                    color = textMuted
                )
            }
        }

        // Bento Card 2: Circular Arc Speed Dial Gauge (Reference Layout: Temperature/Light Arc)
        SpeedDialGauge(
            currentSpeed = prefs.baseSpeed,
            onSpeedChange = { newSpeed ->
                prefs.baseSpeed = newSpeed
            }
        )

        // Bento Grid Row: Physical Keyboard Card & Live Quick Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Physical Keyboard Card
            GlassCard(
                modifier = Modifier.weight(1f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x33FFFFFF) else Color(0x18000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = if (state.hasPhysicalKeyboard) WarmEmerald else textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text("Keyboard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Text(
                        text = if (state.hasPhysicalKeyboard) {
                            state.connectedKeyboardNames.firstOrNull() ?: "Connected"
                        } else {
                            "Plug in USB OTG or Bluetooth"
                        },
                        fontSize = 11.sp,
                        color = textSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            // Floating Panel Toggle Card
            GlassCard(
                modifier = Modifier.weight(1f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x33FFFFFF) else Color(0x18000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = null,
                            tint = WarmGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text("Floating HUD", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Button(
                        onClick = {
                            val next = !prefs.isPanelEnabled
                            prefs.isPanelEnabled = next
                            CursorAccessibilityService.instance?.reloadFloatingPanel()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .motionPress()
                            .testTag("floating_panel_toggle_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prefs.isPanelEnabled) WarmAccentAmber else if (isDark) Color(0xFF281F18) else Color(0xFFE2D6C6)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (prefs.isPanelEnabled) "Active" else "Hidden",
                            fontSize = 11.sp,
                            color = if (prefs.isPanelEnabled) Color(0xFF160F0A) else textPrimary
                        )
                    }
                }
            }
        }

        // Bento Card: Tactile Floating Control Deck Preview (Styled after Reference Image)
        FloatingPanelDeckPreview(
            prefs = prefs,
            modifier = Modifier.testTag("floating_deck_preview_card")
        )

        // Bento Card 4: Cursor Preview Box
        CursorPreviewBox(
            pointerStyle = prefs.pointerStyle,
            cursorSizeDp = prefs.cursorSizeDp,
            cursorColor = prefs.cursorColor,
            cursorOpacity = prefs.cursorOpacity,
            cursorShadow = prefs.cursorShadow
        )

        // Navigation Action Bento Cards
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NavigationOptionCard(
                icon = Icons.Default.Mouse,
                title = "Mouse Controls & Key Mapping",
                subtitle = "Swap buttons, middle-click action, and remap all keys",
                onClick = onNavigateToSettings,
                testTag = "nav_settings_button"
            )

            NavigationOptionCard(
                icon = Icons.Default.Build,
                title = "Interactive Test Sandbox",
                subtitle = "Click counter, context menu, drag & drop, and typing test",
                onClick = onNavigateToTestPad,
                testTag = "nav_test_pad_button"
            )

            NavigationOptionCard(
                icon = Icons.Default.Help,
                title = "Setup Guide & Shortcuts",
                subtitle = "Tablet setup, platform limitations, and cheat sheet",
                onClick = onNavigateToHelp,
                testTag = "nav_help_button"
            )
        }

        Spacer(modifier = Modifier.height(72.dp)) // Space for floating bottom bar
    }
}

@Composable
private fun NavigationOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextWarmPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextWarmSecondary else TextWarmLightSecondary

    GlassCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0x28FFFFFF) else Color(0x18000000))
                        .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x20000000), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = WarmAccentAmber, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Text(subtitle, fontSize = 11.sp, color = textSecondary)
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
