package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmLightPrimary
import com.example.ui.theme.TextWarmLightSecondary

@Composable
fun HelpScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("help_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guide & Documentation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Target Device & Android Compatibility
            item {
                GlassCard(borderColor = CyberCyan) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tablet, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lenovo Tab M10 & Android 10+ Compatibility", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }
                        Text(
                            text = "KeyCursor is purpose-engineered to run with ultra-low RAM (<35MB) and near-zero idle CPU usage on Android 10 (API 29) ARM64 tablets with 3 GB RAM, supporting portrait, landscape, and multi-window environments.",
                            fontSize = 12.sp,
                            color = textSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Quick Setup Instructions
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = CyberIndigo, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. Enabling Accessibility Service", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }
                        Text(
                            text = "1. Tap the button below to open Android Accessibility Settings.\n2. Scroll down to 'Downloaded Services' or 'Installed Services'.\n3. Tap 'KeyCursor' and toggle it ON.\n4. Confirm the system prompt. KeyCursor is now active system-wide!",
                            fontSize = 12.sp,
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
                            colors = ButtonDefaults.buttonColors(containerColor = CyberIndigo),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Accessibility Settings", color = Color.White)
                        }
                    }
                }
            }

            // Keyboard Connection Guide
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Keyboard, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. Hardware Keyboard Connection", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }
                        Text(
                            text = "• USB Keyboards: Connect using a USB Type-C to Type-A OTG adapter. KeyCursor automatically detects the hardware device.\n• Bluetooth Keyboards: Pair via Android Bluetooth settings. When paired, KeyCursor detects keystrokes immediately.",
                            fontSize = 12.sp,
                            color = textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Keyboard Shortcuts Cheatsheet
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("3. Hardware Keyboard Cheatsheet", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                        ShortcutRow("Arrow Keys", "Move cursor smoothly with hold acceleration")
                        ShortcutRow("Shift + Arrow", "Fast Movement Mode (2.4x speed multiplier)")
                        ShortcutRow("Ctrl + Arrow", "Fine Precision Mode (0.35x speed multiplier)")
                        ShortcutRow("Enter", "Left Click at current cursor coordinates")
                        ShortcutRow("Shift + Enter", "Right Click / Context Menu gesture")
                        ShortcutRow("Space (Hold)", "Drag & Drop: move while holding Space, release to drop")
                        ShortcutRow("Page Up / Down", "Scroll vertically")
                        ShortcutRow("Shift + Page Up/Down", "Scroll horizontally")
                        ShortcutRow("Alt + C", "Toggle between Mouse Mode and Normal Typing Mode")
                    }
                }
            }

            // Platform Boundaries & Android Security Notes
            item {
                GlassCard(borderColor = CyberAmber) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("4. Android OS Security & Limitations", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }
                        Text(
                            text = "• Password Fields: For user security, the Android OS prevents AccessibilityServices from injecting synthetic gestures into secure password fields and banking applications (FLAG_SECURE).\n• Native Right-Click: Android OS touch dispatch does not have a separate secondary click API; KeyCursor translates right-click to a contextual long-press (450ms) which triggers menus in all apps.\n• Privacy Guarantee: KeyCursor operates 100% offline. Zero keystrokes or coordinates are transmitted externally.",
                            fontSize = 12.sp,
                            color = textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}

@Composable
private fun ShortcutRow(shortcut: String, description: String) {
    val isDark = LocalIsDarkTheme.current
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0xFF221913) else Color(0xFFEDE5DA))
            .border(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x1F000000), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDark) Color(0xFF33251B) else Color(0xFFE2D6C6))
                .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x28000000), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(shortcut, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = description,
            fontSize = 11.sp,
            color = textSecondary,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
