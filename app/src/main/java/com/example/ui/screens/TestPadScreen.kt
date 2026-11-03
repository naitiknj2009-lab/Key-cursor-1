package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import com.example.ui.animation.motionPress
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.CyberRose
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmLightPrimary
import com.example.ui.theme.TextWarmLightSecondary

@Composable
fun TestPadScreen(
    prefs: KeyCursorPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by CursorStateHolder.state.collectAsState()
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextMuted else TextWarmLightMuted

    var clickCount by remember { mutableIntStateOf(0) }
    var doubleClickCount by remember { mutableIntStateOf(0) }
    var longPressDetected by remember { mutableStateOf(false) }
    var contextMenuOpened by remember { mutableStateOf(false) }
    var dragDroppedInTarget by remember { mutableStateOf(false) }
    var testInputText by remember { mutableStateOf("") }

    val recentActions = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("testpad_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Interactive Test Pad", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text(
                    text = "Live Telemetry: X=${state.x.toInt()}, Y=${state.y.toInt()} | ${state.lastActionDescription}",
                    fontSize = 11.sp,
                    color = CyberCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section 1: Click & Double Click Test
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. Left Click & Double-Click Test", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Left Click Button
                            Button(
                                onClick = {
                                    clickCount++
                                    recentActions.add(0, "Single Click registered (Count: $clickCount)")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .motionPress()
                                    .testTag("test_single_click_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                            ) {
                                Text("Left Click ($clickCount)", color = CyberCyan, fontWeight = FontWeight.Bold)
                            }

                            // Double Click Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(25.dp))
                                    .background(CyberIndigo.copy(alpha = 0.2f))
                                    .border(1.dp, CyberIndigo, RoundedCornerShape(25.dp))
                                    .motionPress()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                doubleClickCount++
                                                recentActions.add(0, "Double Tap registered (Count: $doubleClickCount)")
                                                Toast.makeText(context, "Double Click Success!", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                    .testTag("test_double_click_box"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Double Click ($doubleClickCount)", color = CyberIndigo, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (prefs.swapLeftRightClick) {
                            Text(
                                text = "Note: 'Swap Left & Right Click' is ON. The secondary button triggers this tap.",
                                fontSize = 11.sp,
                                color = CyberAmber
                            )
                        }
                    }
                }
            }

            // Section 1.5: Middle Click Action Test
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Middle-Click Action: ${prefs.middleClickAction.displayName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text(
                            text = "Mapped key: ${KeyCursorPreferences.getKeyDisplayName(prefs.keyMiddleClick)}. Press this key to trigger the action (${prefs.middleClickAction.description}).",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Section 2: Right Click (Context Menu) & Long Press
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mouse, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. Context Menu (Right Click) & Long Press", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Text(
                            text = "Position cursor here and press Shift + Enter (or hold Enter) to open the context menu equivalent.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, if (contextMenuOpened) CyberEmerald else Color(0xFF334155), RoundedCornerShape(10.dp))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            longPressDetected = true
                                            contextMenuOpened = true
                                            recentActions.add(0, "Context Menu / Long Press activated")
                                        }
                                    )
                                }
                                .padding(12.dp)
                                .testTag("test_context_menu_box"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (contextMenuOpened) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Context Menu Gesture Detected!", color = CyberEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            } else {
                                Text("Right-Click / Long-Press Here", color = TextSecondary, fontSize = 13.sp)
                            }
                        }

                        if (contextMenuOpened) {
                            OutlinedButton(
                                onClick = { contextMenuOpened = false },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Reset Menu", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Section 3: Drag & Drop Sandbox
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("3. Drag & Drop Test", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = "Hold Space while moving arrow keys from Drop Zone A to Drop Zone B, then release Space.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Color(0xFF475569), RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Zone A\n(Start Drag)", fontSize = 12.sp, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (dragDroppedInTarget) CyberEmerald.copy(alpha = 0.2f) else Color(0xFF0F172A))
                                    .border(1.dp, if (dragDroppedInTarget) CyberEmerald else CyberCyan, RoundedCornerShape(10.dp))
                                    .clickable {
                                        dragDroppedInTarget = true
                                        recentActions.add(0, "Drop completed into Zone B")
                                    }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (dragDroppedInTarget) "Item Dropped!\nSuccess" else "Zone B\n(Drop Target)",
                                    fontSize = 12.sp,
                                    color = if (dragDroppedInTarget) CyberEmerald else CyberCyan,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Typing Mode Conflict Prevention Test
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Keyboard, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("4. Typing Mode & Conflict Prevention", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Text(
                            text = "To type without moving the mouse pointer, press Alt + C or use the Floating Panel to switch into 'Typing Mode'. Then type in this input box.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )

                        OutlinedTextField(
                            value = testInputText,
                            onValueChange = { testInputText = it },
                            label = { Text("Physical keyboard input test...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_input_text_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Section 5: Vertical Scroll Test
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("5. Vertical Scroll Sandbox", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = "Place cursor inside this box and press Page Up / Page Down to test scrolling gestures.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF070B12))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        ) {
                            LazyColumn(modifier = Modifier.padding(8.dp)) {
                                items(25) { index ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp, horizontal = 8.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF121927))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Scroll item #${index + 1}", fontSize = 12.sp, color = TextPrimary)
                                        Text("Index $index", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 6: Real-time Event Log
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recent Event Log", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                color = CyberRose,
                                modifier = Modifier.clickable { recentActions.clear() }
                            )
                        }

                        if (recentActions.isEmpty()) {
                            Text("No interaction recorded yet. Tap or drag above.", fontSize = 12.sp, color = TextMuted)
                        } else {
                            recentActions.take(5).forEach { action ->
                                Text("• $action", fontSize = 12.sp, color = CyberCyan)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}
