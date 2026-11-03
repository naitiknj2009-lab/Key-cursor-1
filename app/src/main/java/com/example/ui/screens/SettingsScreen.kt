package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DragTriggerMode
import com.example.data.KeyCursorPreferences
import com.example.data.MiddleClickAction
import com.example.data.PointerStyle
import com.example.data.SpeedPreset
import com.example.service.CursorAccessibilityService
import com.example.ui.animation.motionPress
import com.example.ui.components.CursorPreviewBox
import com.example.ui.components.GlassCard
import com.example.ui.components.KeyCustomizeDialog
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRose
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmLightPrimary
import com.example.ui.theme.TextWarmLightSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    prefs: KeyCursorPreferences,
    onBack: () -> Unit,
    onThemeChanged: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextMuted else TextWarmLightMuted

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cursor", "Mouse Controls", "Key Mapping", "Movement", "Floating Panel", "Automation")

    // State bindings
    var pointerStyle by remember { mutableStateOf(prefs.pointerStyle) }
    var cursorSize by remember { mutableIntStateOf(prefs.cursorSizeDp) }
    var cursorColor by remember { mutableLongStateOf(prefs.cursorColor) }
    var cursorOpacity by remember { mutableFloatStateOf(prefs.cursorOpacity) }
    var cursorShadow by remember { mutableStateOf(prefs.cursorShadow) }
    var clickAnimation by remember { mutableStateOf(prefs.clickAnimation) }
    var dragAnimation by remember { mutableStateOf(prefs.dragAnimation) }

    // Mouse Controls
    var swapLeftRight by remember { mutableStateOf(prefs.swapLeftRightClick) }
    var invertScroll by remember { mutableStateOf(prefs.invertScrollDirection) }
    var middleClickAction by remember { mutableStateOf(prefs.middleClickAction) }
    var clickDuration by remember { mutableLongStateOf(prefs.clickDurationMs) }
    var doubleClickEnabled by remember { mutableStateOf(prefs.doubleClickEnabled) }
    var doubleClickInterval by remember { mutableLongStateOf(prefs.doubleClickIntervalMs) }
    var longPressDuration by remember { mutableLongStateOf(prefs.longPressDurationMs) }
    var dragMode by remember { mutableStateOf(prefs.dragTriggerMode) }
    var scrollStep by remember { mutableIntStateOf(prefs.scrollStepPx) }

    // Movement
    var baseSpeed by remember { mutableFloatStateOf(prefs.baseSpeed) }
    var acceleration by remember { mutableFloatStateOf(prefs.acceleration) }
    var speedPreset by remember { mutableStateOf(prefs.speedPreset) }
    var fastMultiplier by remember { mutableFloatStateOf(prefs.fastMultiplier) }
    var fineMultiplier by remember { mutableFloatStateOf(prefs.fineMultiplier) }
    var diagonalEnabled by remember { mutableStateOf(prefs.diagonalEnabled) }

    // Panel
    var panelEnabled by remember { mutableStateOf(prefs.isPanelEnabled) }
    var panelCompact by remember { mutableStateOf(prefs.isPanelCompact) }
    var panelAlpha by remember { mutableFloatStateOf(prefs.panelAlpha) }

    // Automation
    var autoActivate by remember { mutableStateOf(prefs.autoActivateOnConnect) }
    var autoDeactivate by remember { mutableStateOf(prefs.autoDeactivateOnDisconnect) }
    var inactivityTimeout by remember { mutableIntStateOf(prefs.inactivityTimeoutSeconds) }

    // Key Customization Modal State
    var remapActionName by remember { mutableStateOf<String?>(null) }
    var remapCurrentKeyCode by remember { mutableIntStateOf(0) }
    var onKeyRemappedCallback by remember { mutableStateOf<((Int) -> Unit)?>(null) }

    val scrollState = rememberScrollState()

    // Dialog for remapping keys
    if (remapActionName != null && onKeyRemappedCallback != null) {
        KeyCustomizeDialog(
            actionName = remapActionName!!,
            currentKeyCode = remapCurrentKeyCode,
            onKeySelected = { newKey ->
                onKeyRemappedCallback?.invoke(newKey)
                Toast.makeText(
                    context,
                    "$remapActionName mapped to ${KeyCursorPreferences.getKeyDisplayName(newKey)}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                remapActionName = null
                onKeyRemappedCallback = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .motionPress()
                    .testTag("settings_back_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Controls & Customization",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "Full Mouse & Hardware Key Remapping",
                    fontSize = 11.sp,
                    color = CyberCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Framer Motion-style Spring Animated Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = if (isDark) Color(0xFF080C14) else Color(0xFFEDE4D8),
            contentColor = CyberCyan,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.motionPress(),
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) CyberCyan else textSecondary
                        )
                    }
                )
            }
        }

        // Content Area with Framer Motion Layout Smooth Resizing
        Column(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: CURSOR STYLE & APPEARANCE
                    CursorPreviewBox(
                        pointerStyle = pointerStyle,
                        cursorSizeDp = cursorSize,
                        cursorColor = cursorColor,
                        cursorOpacity = cursorOpacity,
                        cursorShadow = cursorShadow
                    )

                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("Pointer Style", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            PointerStyle.values().forEach { style ->
                                val isSelected = pointerStyle == style
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color(0xFF131A26))
                                        .border(1.dp, if (isSelected) CyberCyan else Color.Transparent, RoundedCornerShape(10.dp))
                                        .motionPress(onClick = {
                                            pointerStyle = style
                                            prefs.pointerStyle = style
                                            CursorAccessibilityService.instance?.refreshCursorStyle()
                                        })
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(style.displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        Text(style.description, fontSize = 11.sp, color = TextSecondary)
                                    }
                                    if (isSelected) {
                                        Text("Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                                    }
                                }
                            }
                        }
                    }

                    // Cursor Dimensions
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Dimensions & Transparency", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                            Text("Size: ${cursorSize}dp", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = cursorSize.toFloat(),
                                onValueChange = {
                                    cursorSize = it.toInt()
                                    prefs.cursorSizeDp = it.toInt()
                                    CursorAccessibilityService.instance?.refreshCursorStyle()
                                },
                                valueRange = 16f..64f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )

                            Text("Opacity: ${(cursorOpacity * 100).toInt()}%", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = cursorOpacity,
                                onValueChange = {
                                    cursorOpacity = it
                                    prefs.cursorOpacity = it
                                    CursorAccessibilityService.instance?.refreshCursorStyle()
                                },
                                valueRange = 0.2f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )
                        }
                    }

                    // Colors
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Pointer Color", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            val colorPresets = listOf(
                                0xFF00E5FF to "Neon Cyan",
                                0xFF10B981 to "Emerald",
                                0xFFF59E0B to "Amber",
                                0xFFF43F5E to "Cyber Rose",
                                0xFFA855F7 to "Purple Glow",
                                0xFF3B82F6 to "Electric Blue",
                                0xFFFFFFFF to "Pure White"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                colorPresets.forEach { (colorVal, _) ->
                                    val isPicked = cursorColor == colorVal
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(colorVal))
                                            .border(
                                                if (isPicked) 3.dp else 1.dp,
                                                if (isPicked) Color.White else Color(0x44FFFFFF),
                                                CircleShape
                                            )
                                            .motionPress(onClick = {
                                                cursorColor = colorVal
                                                prefs.cursorColor = colorVal
                                                CursorAccessibilityService.instance?.refreshCursorStyle()
                                            })
                                    )
                                }
                            }
                        }
                    }

                    // Feedback
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Visual Feedback & Shadows", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                            SettingSwitchRow("Pointer Drop Shadow", "Render physical shadow underneath cursor", cursorShadow) {
                                cursorShadow = it
                                prefs.cursorShadow = it
                                CursorAccessibilityService.instance?.refreshCursorStyle()
                            }

                            SettingSwitchRow("Click Ripple Animation", "Framer Motion style wave on click", clickAnimation) {
                                clickAnimation = it
                                prefs.clickAnimation = it
                            }

                            SettingSwitchRow("Drag Aura Animation", "Pulsing amber aura while dragging", dragAnimation) {
                                dragAnimation = it
                                prefs.dragAnimation = it
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: MOUSE CONTROLS CUSTOMIZATION
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("Mouse Button Customization", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                            SettingSwitchRow(
                                "Swap Left & Right Click",
                                "Primary key performs context menu; secondary performs tap",
                                swapLeftRight
                            ) {
                                swapLeftRight = it
                                prefs.swapLeftRightClick = it
                            }

                            SettingSwitchRow(
                                "Natural / Inverted Scrolling",
                                "Invert Page Up / Page Down scroll gesture direction",
                                invertScroll
                            ) {
                                invertScroll = it
                                prefs.invertScrollDirection = it
                            }
                        }
                    }

                    // Middle Click Action Configuration
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Middle-Click Shortcut Behavior", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Choose what system action occurs when you press the Middle-Click key:", fontSize = 12.sp, color = TextSecondary)

                            MiddleClickAction.values().forEach { action ->
                                val isSelected = middleClickAction == action
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyberIndigo.copy(alpha = 0.2f) else Color(0xFF131A26))
                                        .border(1.dp, if (isSelected) CyberIndigo else Color.Transparent, RoundedCornerShape(8.dp))
                                        .motionPress(onClick = {
                                            middleClickAction = action
                                            prefs.middleClickAction = action
                                        })
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(action.displayName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        Text(action.description, fontSize = 11.sp, color = TextSecondary)
                                    }
                                    if (isSelected) {
                                        Text("Active", fontSize = 11.sp, color = CyberIndigo, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Drag Mode & Click Durations
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Click & Drag Timing", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                            Text("Tap Duration: ${clickDuration}ms", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = clickDuration.toFloat(),
                                onValueChange = {
                                    clickDuration = it.toLong()
                                    prefs.clickDurationMs = it.toLong()
                                },
                                valueRange = 30f..180f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )

                            SettingSwitchRow("Double-Click Detection", "Trigger double tap on rapid Enter", doubleClickEnabled) {
                                doubleClickEnabled = it
                                prefs.doubleClickEnabled = it
                            }

                            if (doubleClickEnabled) {
                                Text("Double-Click Timing Window: ${doubleClickInterval}ms", fontSize = 13.sp, color = TextSecondary)
                                Slider(
                                    value = doubleClickInterval.toFloat(),
                                    onValueChange = {
                                        doubleClickInterval = it.toLong()
                                        prefs.doubleClickIntervalMs = it.toLong()
                                    },
                                    valueRange = 150f..500f,
                                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                                )
                            }

                            Text("Long Press Duration: ${longPressDuration}ms", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = longPressDuration.toFloat(),
                                onValueChange = {
                                    longPressDuration = it.toLong()
                                    prefs.longPressDurationMs = it.toLong()
                                },
                                valueRange = 300f..1000f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )
                        }
                    }

                    // Drag Trigger
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Drag and Drop Trigger Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            DragTriggerMode.values().forEach { mode ->
                                val isSelected = dragMode == mode
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else Color(0xFF131A26))
                                        .border(1.dp, if (isSelected) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                        .motionPress(onClick = {
                                            dragMode = mode
                                            prefs.dragTriggerMode = mode
                                        })
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(mode.displayName, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                    if (isSelected) {
                                        Text("Selected", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: INTERACTIVE HARDWARE KEY CUSTOMIZATION
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Interactive Key Mapper", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Tap any action below to bind a new physical keyboard key", fontSize = 11.sp, color = CyberCyan)
                                }
                                Icon(Icons.Default.Keyboard, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            }

                            CustomKeyMappingRow("Left Click (Primary)", prefs.keyLeftClick) {
                                remapActionName = "Left Click (Primary)"
                                remapCurrentKeyCode = prefs.keyLeftClick
                                onKeyRemappedCallback = { newKey -> prefs.keyLeftClick = newKey }
                            }

                            CustomKeyMappingRow("Right Click (Context Menu)", prefs.keyRightClick) {
                                remapActionName = "Right Click (Context Menu)"
                                remapCurrentKeyCode = prefs.keyRightClick
                                onKeyRemappedCallback = { newKey -> prefs.keyRightClick = newKey }
                            }

                            CustomKeyMappingRow("Middle Click Action", prefs.keyMiddleClick) {
                                remapActionName = "Middle Click Action"
                                remapCurrentKeyCode = prefs.keyMiddleClick
                                onKeyRemappedCallback = { newKey -> prefs.keyMiddleClick = newKey }
                            }

                            CustomKeyMappingRow("Dedicated Double Click", prefs.keyDoubleClick) {
                                remapActionName = "Dedicated Double Click"
                                remapCurrentKeyCode = prefs.keyDoubleClick
                                onKeyRemappedCallback = { newKey -> prefs.keyDoubleClick = newKey }
                            }

                            CustomKeyMappingRow("Drag & Drop Key", prefs.keyDrag) {
                                remapActionName = "Drag & Drop Key"
                                remapCurrentKeyCode = prefs.keyDrag
                                onKeyRemappedCallback = { newKey -> prefs.keyDrag = newKey }
                            }

                            CustomKeyMappingRow("Move Up", prefs.keyUp) {
                                remapActionName = "Move Up"
                                remapCurrentKeyCode = prefs.keyUp
                                onKeyRemappedCallback = { newKey -> prefs.keyUp = newKey }
                            }

                            CustomKeyMappingRow("Move Down", prefs.keyDown) {
                                remapActionName = "Move Down"
                                remapCurrentKeyCode = prefs.keyDown
                                onKeyRemappedCallback = { newKey -> prefs.keyDown = newKey }
                            }

                            CustomKeyMappingRow("Move Left", prefs.keyLeft) {
                                remapActionName = "Move Left"
                                remapCurrentKeyCode = prefs.keyLeft
                                onKeyRemappedCallback = { newKey -> prefs.keyLeft = newKey }
                            }

                            CustomKeyMappingRow("Move Right", prefs.keyRight) {
                                remapActionName = "Move Right"
                                remapCurrentKeyCode = prefs.keyRight
                                onKeyRemappedCallback = { newKey -> prefs.keyRight = newKey }
                            }

                            CustomKeyMappingRow("Scroll Up", prefs.keyScrollUp) {
                                remapActionName = "Scroll Up"
                                remapCurrentKeyCode = prefs.keyScrollUp
                                onKeyRemappedCallback = { newKey -> prefs.keyScrollUp = newKey }
                            }

                            CustomKeyMappingRow("Scroll Down", prefs.keyScrollDown) {
                                remapActionName = "Scroll Down"
                                remapCurrentKeyCode = prefs.keyScrollDown
                                onKeyRemappedCallback = { newKey -> prefs.keyScrollDown = newKey }
                            }

                            CustomKeyMappingRow("Toggle Mouse / Typing Mode", prefs.keyToggleCursor) {
                                remapActionName = "Toggle Mouse / Typing Mode"
                                remapCurrentKeyCode = prefs.keyToggleCursor
                                onKeyRemappedCallback = { newKey -> prefs.keyToggleCursor = newKey }
                            }

                            OutlinedButton(
                                onClick = {
                                    prefs.keyUp = android.view.KeyEvent.KEYCODE_DPAD_UP
                                    prefs.keyDown = android.view.KeyEvent.KEYCODE_DPAD_DOWN
                                    prefs.keyLeft = android.view.KeyEvent.KEYCODE_DPAD_LEFT
                                    prefs.keyRight = android.view.KeyEvent.KEYCODE_DPAD_RIGHT
                                    prefs.keyLeftClick = android.view.KeyEvent.KEYCODE_ENTER
                                    prefs.keyRightClick = android.view.KeyEvent.KEYCODE_BUTTON_R1
                                    prefs.keyMiddleClick = android.view.KeyEvent.KEYCODE_M
                                    prefs.keyDoubleClick = android.view.KeyEvent.KEYCODE_UNKNOWN
                                    prefs.keyDrag = android.view.KeyEvent.KEYCODE_SPACE
                                    prefs.keyScrollUp = android.view.KeyEvent.KEYCODE_PAGE_UP
                                    prefs.keyScrollDown = android.view.KeyEvent.KEYCODE_PAGE_DOWN
                                    prefs.keyToggleCursor = android.view.KeyEvent.KEYCODE_C
                                    Toast.makeText(context, "All key mappings restored to default", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .motionPress()
                            ) {
                                Text("Restore All Key Mappings to Default", color = CyberCyan)
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: MOVEMENT & ACCELERATION
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Movement Engine & Speed", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                            Text("Base Speed: ${baseSpeed.toInt()} px/tick", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = baseSpeed,
                                onValueChange = {
                                    baseSpeed = it
                                    prefs.baseSpeed = it
                                },
                                valueRange = 6f..36f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )

                            Text("Hold Acceleration: ${String.format("%.1f", acceleration)}x", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = acceleration,
                                onValueChange = {
                                    acceleration = it
                                    prefs.acceleration = it
                                },
                                valueRange = 1.0f..3.5f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )

                            Text("Fast Multiplier (Shift + Arrow): ${String.format("%.1f", fastMultiplier)}x", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = fastMultiplier,
                                onValueChange = {
                                    fastMultiplier = it
                                    prefs.fastMultiplier = it
                                },
                                valueRange = 1.5f..4.0f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )

                            Text("Fine Multiplier (Ctrl + Arrow): ${String.format("%.2f", fineMultiplier)}x", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = fineMultiplier,
                                onValueChange = {
                                    fineMultiplier = it
                                    prefs.fineMultiplier = it
                                },
                                valueRange = 0.1f..0.7f,
                                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                            )

                            SettingSwitchRow("Diagonal Movement", "Normalize simultaneous 2-key directional movement", diagonalEnabled) {
                                diagonalEnabled = it
                                prefs.diagonalEnabled = it
                            }
                        }
                    }
                }

                4 -> {
                    // TAB 4: FLOATING PANEL
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Floating Control Bar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                            SettingSwitchRow("Enable Floating Panel", "Show movable on-screen widget across all apps", panelEnabled) {
                                panelEnabled = it
                                prefs.isPanelEnabled = it
                                CursorAccessibilityService.instance?.reloadFloatingPanel()
                            }

                            SettingSwitchRow("Compact Pill Mode", "Collapse panel into tiny unobtrusive pill", panelCompact) {
                                panelCompact = it
                                prefs.isPanelCompact = it
                                CursorAccessibilityService.instance?.reloadFloatingPanel()
                            }

                            Text("Panel Opacity: ${(panelAlpha * 100).toInt()}%", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = panelAlpha,
                                onValueChange = {
                                    panelAlpha = it
                                    prefs.panelAlpha = it
                                    CursorAccessibilityService.instance?.reloadFloatingPanel()
                                },
                                valueRange = 0.3f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = CyberIndigo, activeTrackColor = CyberIndigo)
                            )

                            Button(
                                onClick = {
                                    prefs.panelX = 40
                                    prefs.panelY = 180
                                    CursorAccessibilityService.instance?.reloadFloatingPanel()
                                    Toast.makeText(context, "Floating panel position reset", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .motionPress()
                            ) {
                                Text("Reset Panel Screen Position", color = TextPrimary)
                            }
                        }
                    }
                }

                5 -> {
                    // TAB 5: AUTOMATION, THEME & RESET
                    // Theme Mode Card
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Appearance & Theme", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                            SettingSwitchRow(
                                title = "Dark Mode",
                                subtitle = if (isDark) "Warm Obsidian Glassmorphism (Dark)" else "Warm Cream & Frosted Ivory (Light Mode)",
                                checked = isDark
                            ) { enabled ->
                                prefs.isDarkTheme = enabled
                                onThemeChanged?.invoke(enabled)
                            }
                        }
                    }

                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Smart Keyboard Automation", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                            SettingSwitchRow(
                                "Auto-Activate on Keyboard Connect",
                                "Turn on cursor when USB or Bluetooth keyboard attaches",
                                autoActivate
                            ) {
                                autoActivate = it
                                prefs.autoActivateOnConnect = it
                            }

                            SettingSwitchRow(
                                "Auto-Deactivate on Disconnect",
                                "Pause cursor when physical keyboard unplugs",
                                autoDeactivate
                            ) {
                                autoDeactivate = it
                                prefs.autoDeactivateOnDisconnect = it
                            }

                            Text("Inactivity Auto-Hide", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            val timeouts = listOf(0 to "Off", 30 to "30s", 60 to "60s", 120 to "2m")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                timeouts.forEach { (sec, label) ->
                                    val isSelected = inactivityTimeout == sec
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                            .border(1.dp, if (isSelected) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                            .motionPress(onClick = {
                                                inactivityTimeout = sec
                                                prefs.inactivityTimeoutSeconds = sec
                                            })
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(label, fontSize = 12.sp, color = if (isSelected) CyberCyan else TextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // Reset Factory Defaults
                    GlassCard(borderColor = CyberRose.copy(alpha = 0.5f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Reset Factory Defaults", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CyberRose)
                            Text("Restore all KeyCursor preferences, controls, speeds, and coordinates back to clean defaults.", fontSize = 12.sp, color = TextSecondary)
                            Button(
                                onClick = {
                                    prefs.resetToDefaults()
                                    pointerStyle = prefs.pointerStyle
                                    cursorSize = prefs.cursorSizeDp
                                    cursorColor = prefs.cursorColor
                                    cursorOpacity = prefs.cursorOpacity
                                    cursorShadow = prefs.cursorShadow
                                    swapLeftRight = prefs.swapLeftRightClick
                                    invertScroll = prefs.invertScrollDirection
                                    middleClickAction = prefs.middleClickAction
                                    baseSpeed = prefs.baseSpeed
                                    acceleration = prefs.acceleration
                                    speedPreset = prefs.speedPreset
                                    panelEnabled = prefs.isPanelEnabled
                                    autoActivate = prefs.autoActivateOnConnect
                                    autoDeactivate = prefs.autoDeactivateOnDisconnect
                                    CursorAccessibilityService.instance?.refreshCursorStyle()
                                    CursorAccessibilityService.instance?.reloadFloatingPanel()
                                    Toast.makeText(context, "All settings reset to defaults", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberRose.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .motionPress()
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = CyberRose, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reset All Settings", color = CyberRose, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(76.dp))
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textPrimary)
            Text(subtitle, fontSize = 11.sp, color = textSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberCyan,
                checkedTrackColor = CyberCyan.copy(alpha = 0.35f),
                uncheckedThumbColor = if (isDark) Color(0xFF64748B) else Color(0xFF9E8D7D),
                uncheckedTrackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2D6C6)
            )
        )
    }
}

@Composable
private fun CustomKeyMappingRow(
    actionName: String,
    keyCode: Int,
    onRemapClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextMuted else TextWarmLightMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0xFF0F1522) else Color(0xFFEDE4D8))
            .border(1.dp, if (isDark) Color(0x22FFFFFF) else Color(0x1F000000), RoundedCornerShape(10.dp))
            .motionPress(onClick = onRemapClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(actionName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
            Text("Tap to remap", fontSize = 10.sp, color = textMuted)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2D6C6))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = KeyCursorPreferences.getKeyDisplayName(keyCode),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = textSecondary, modifier = Modifier.size(16.dp))
        }
    }
}
