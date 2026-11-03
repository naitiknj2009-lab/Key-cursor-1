package com.naitik.keycursor

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Brown = Color(0xFF3C241D)
private val Cream = Color(0xFFFFE8D0)
private val Peach = Color(0xFFFFC49A)
private val Amber = Color(0xFFFF9B55)
private val Glass = Color(0x66FFF4E8)
private val Muted = Color(0xFF795F55)
private val Ink = Color(0xFF35231F)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KeyCursorTheme { KeyCursorRoot() } }
    }
}

@Composable
private fun KeyCursorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Brown, secondary = Amber, background = Cream,
            surface = Color(0xFFFFF8F0), onPrimary = Cream,
            onBackground = Ink, onSurface = Ink
        ),
        content = content
    )
}

@Composable
private fun KeyCursorRoot() {
    var page by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("keycursor", 0) }
    var enabled by remember { mutableStateOf(prefs.getBoolean("enabled", true)) }
    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFFFFE9D3), Color(0xFFDDAA8B), Color(0xFF9A6048))))) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xAAFFF7ED)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.NearMe, null, tint = Brown, modifier = Modifier.size(23.dp))
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("KEYCURSOR", color = Brown, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Pointer, your way.", color = Brown.copy(alpha = .72f), fontSize = 12.sp)
                }
                IconButton(onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }) {
                    Icon(Icons.Default.Settings, "Accessibility settings", tint = Brown)
                }
            }
            AnimatedContent(targetState = page, label = "page", modifier = Modifier.weight(1f)) { selected ->
                when (selected) {
                    0 -> Dashboard(enabled, { enabled = it; prefs.edit().putBoolean("enabled", it).apply() }) {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                    1 -> SettingsScreen(prefs)
                    2 -> TestScreen()
                    else -> HelpScreen { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)
                    .shadow(16.dp, RoundedCornerShape(26.dp)).clip(RoundedCornerShape(26.dp))
                    .background(Color(0xCC3B2822)).padding(7.dp),
                horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
            ) {
                val icons = listOf(Icons.Default.Home, Icons.Default.Tune, Icons.Default.Games, Icons.Default.HelpOutline)
                val labels = listOf("Home", "Settings", "Test", "Help")
                labels.forEachIndexed { index, label ->
                    val active = page == index
                    val scale by animateFloatAsState(if (active) 1f else .92f, spring(stiffness = Spring.StiffnessMedium), label = "nav")
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                            .background(if (active) Color(0xFFFFD98F) else Color.Transparent)
                            .clickable { page = index }.padding(vertical = 9.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(icons[index], null, tint = if (active) Brown else Cream.copy(alpha = .8f), modifier = Modifier.size((20 * scale).dp))
                        Spacer(Modifier.height(3.dp))
                        Text(label, color = if (active) Brown else Cream.copy(alpha = .8f), fontSize = 10.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
        content = content
    )
}

@Composable
private fun Heading(eyebrow: String, title: String, subtitle: String) {
    Text(eyebrow.uppercase(), color = Brown.copy(alpha = .65f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    Spacer(Modifier.height(6.dp))
    Text(title, color = Brown, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(5.dp))
    Text(subtitle, color = Brown.copy(alpha = .7f), fontSize = 13.sp, lineHeight = 19.sp)
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.fillMaxWidth().padding(top = 14.dp).shadow(12.dp, RoundedCornerShape(25.dp))
            .clip(RoundedCornerShape(25.dp)).background(Glass)
            .border(1.dp, Color(0x99FFFFFF), RoundedCornerShape(25.dp))
            .padding(17.dp).animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        content = content
    )
}

@Composable
private fun Dashboard(enabled: Boolean, onEnabled: (Boolean) -> Unit, openAccessibility: () -> Unit) {
    ScreenColumn {
        Heading("Your keyboard, reimagined", "A pointer that moves with you.", "Control Android with the keyboard you already own.")
        GlassCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("KeyCursor control", color = Brown, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (enabled) "Ready when the service is enabled" else "Pointer controls paused", color = Muted, fontSize = 11.sp)
                }
                Switch(checked = enabled, onCheckedChange = onEnabled, colors = SwitchDefaults.colors(checkedThumbColor = Cream, checkedTrackColor = Brown))
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFD98F)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Mouse, null, tint = Brown)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Accessibility service", color = Brown, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Needed for pointer control across apps", color = Muted, fontSize = 11.sp)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Brown)
            }
            Spacer(Modifier.height(13.dp))
            Button(onClick = openAccessibility, modifier = Modifier.fillMaxWidth().height(49.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Brown, contentColor = Cream)) {
                Text("Set up KeyCursor", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp)); Icon(Icons.Default.ArrowOutward, null, modifier = Modifier.size(17.dp))
            }
        }
        GlassCard {
            Text("QUICK START", color = Brown.copy(alpha = .65f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
            val steps = listOf("Connect your USB OTG or Bluetooth keyboard", "Enable KeyCursor in Accessibility settings", "Use arrow keys to move and Enter to click")
            steps.forEachIndexed { index, step ->
                Row(Modifier.padding(top = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(27.dp).clip(CircleShape).background(Color(0xFFFFD98F)), contentAlignment = Alignment.Center) {
                        Text("${index + 1}", color = Brown, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(11.dp)); Text(step, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
        GlassCard {
            Text("YOUR SHORTCUTS", color = Brown.copy(alpha = .65f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
            Row(Modifier.fillMaxWidth().padding(top = 13.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                ShortcutChip("↑ ↓ ← →", "Move")
                ShortcutChip("Enter", "Click")
                ShortcutChip("Shift", "Fast")
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Offline by design  •  No account  •  Android 10+", color = Brown.copy(alpha = .7f), fontSize = 11.sp)
    }
}

@Composable
private fun ShortcutChip(key: String, label: String) {
    Column(Modifier.clip(RoundedCornerShape(15.dp)).background(Color(0x55FFFFFF)).padding(horizontal = 13.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(key, color = Brown, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun SettingsScreen(prefs: android.content.SharedPreferences) {
    var speed by remember { mutableFloatStateOf(prefs.getFloat("speed", 1f)) }
    var pointerSize by remember { mutableFloatStateOf(prefs.getFloat("size", 32f)) }
    var hud by remember { mutableStateOf(prefs.getBoolean("hud", true)) }
    var typing by remember { mutableStateOf(prefs.getBoolean("typing", false)) }
    ScreenColumn {
        Heading("Make it yours", "Settings", "A few thoughtful adjustments to make the pointer feel right.")
        GlassCard {
            Text("MOVEMENT", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Pointer speed", color = Brown, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f)); Text("%.1f×".format(speed), color = Brown, fontWeight = FontWeight.Bold) }
            Slider(value = speed, onValueChange = { speed = it; prefs.edit().putFloat("speed", it).apply() }, valueRange = .5f..2.5f, colors = SliderDefaults.colors(thumbColor = Brown, activeTrackColor = Brown))
            Divider(color = Color(0x55FFFFFF))
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Pointer size", color = Brown, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f)); Text("${pointerSize.toInt()} dp", color = Brown, fontWeight = FontWeight.Bold) }
            Slider(value = pointerSize, onValueChange = { pointerSize = it; prefs.edit().putFloat("size", it).apply() }, valueRange = 16f..64f, colors = SliderDefaults.colors(thumbColor = Brown, activeTrackColor = Brown))
        }
        GlassCard {
            Text("CONTROL PANEL", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
            SettingToggle("Floating HUD", "Show the compact on-screen control panel", hud) { hud = it; prefs.edit().putBoolean("hud", it).apply() }
            SettingToggle("Start in typing mode", "Let normal keyboard input pass through", typing) { typing = it; prefs.edit().putBoolean("typing", it).apply() }
        }
        GlassCard {
            Text("KEYBOARD MODIFIERS", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
            Text("Shift + arrows  ·  Fast movement", color = Brown, modifier = Modifier.padding(top = 12.dp), fontSize = 13.sp)
            Text("Ctrl + arrows  ·  Fine movement", color = Brown, modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
            Text("Alt + C  ·  Toggle typing mode", color = Brown, modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
        }
    }
}

@Composable
private fun SettingToggle(title: String, subtitle: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Brown, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
        Switch(checked = value, onCheckedChange = onChange, colors = SwitchDefaults.colors(checkedThumbColor = Cream, checkedTrackColor = Brown))
    }
}

@Composable
private fun TestScreen() {
    ScreenColumn {
        Heading("Try it out", "Test Pad", "Learn the default keyboard shortcuts before using KeyCursor elsewhere.")
        GlassCard {
            Text("KEYBOARD SHORTCUTS", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
            listOf("Arrow keys" to "Move pointer", "Shift + arrows" to "Move faster", "Ctrl + arrows" to "Fine movement", "Enter" to "Tap / click", "Shift + Enter" to "Long-press", "Space + arrows" to "Drag gesture", "Page Up / Down" to "Scroll", "Alt + C" to "Typing mode").forEach { (key, action) ->
                Row(Modifier.fillMaxWidth().padding(top = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(key, color = Brown, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text(action, color = Muted, fontSize = 12.sp)
                }
            }
        }
        GlassCard {
            Text("READY TO TEST?", color = Brown, fontWeight = FontWeight.SemiBold)
            Text("Once the accessibility service is active, connect a physical keyboard and test the shortcuts above.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
private fun HelpScreen(openAccessibility: () -> Unit) {
    ScreenColumn {
        Heading("A little guidance", "Help & setup", "Get KeyCursor working on your Android tablet.")
        GlassCard {
            Text("FIRST-TIME SETUP", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
            listOf("Install the APK and open KeyCursor", "Tap the setup button below", "Find KeyCursor under installed accessibility services", "Turn the service on and confirm the system prompt", "Connect your keyboard and use the arrow keys").forEachIndexed { i, item ->
                Row(Modifier.padding(top = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${i + 1}.", color = Brown, fontWeight = FontWeight.Bold, modifier = Modifier.width(25.dp))
                    Text(item, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = openAccessibility, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = Brown, contentColor = Cream)) {
                Text("Open Accessibility Settings")
            }
        }
        GlassCard {
            Text("ANDROID LIMITATIONS", color = Brown, fontWeight = FontWeight.SemiBold)
            Text("Android accessibility gestures simulate touch. Right-click is represented by a long-press. Secure screens and password fields may block injected gestures.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 7.dp))
        }
    }
}
