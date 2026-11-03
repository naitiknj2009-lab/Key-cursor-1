package com.example.ui.components

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.KeyCursorPreferences
import com.example.ui.animation.MotionSprings
import com.example.ui.animation.motionGlow
import com.example.ui.animation.motionPress
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeyCustomizeDialog(
    actionName: String,
    currentKeyCode: Int,
    onKeySelected: (newKeyCode: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textPrimary = if (isDark) TextPrimary else TextWarmLightPrimary
    val textSecondary = if (isDark) TextSecondary else TextWarmLightSecondary
    val textMuted = if (isDark) TextMuted else TextWarmLightMuted

    var detectedKeyCode by remember { mutableIntStateOf(currentKeyCode) }
    var isListeningLive by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = if (isDark) Color(0xFF16120E) else Color(0xFFFAF6F0),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyan.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    val nativeEvent = keyEvent.nativeKeyEvent
                    if (nativeEvent.action == KeyEvent.ACTION_DOWN) {
                        detectedKeyCode = nativeEvent.keyCode
                        true
                    } else {
                        false
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Remap Action",
                            fontSize = 13.sp,
                            color = CyberCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = actionName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textSecondary)
                    }
                }

                // Live Physical Key Interception Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0xFF241B15) else Color(0xFFEDE4D8))
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .motionGlow(CyberCyan, 18f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Press any key on your physical keyboard",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF090D15))
                                .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = KeyCursorPreferences.getKeyDisplayName(detectedKeyCode),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        }
                    }
                }

                // Quick Popular Keys Catalog
                Text(
                    text = "Or Pick From Common Keys",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                val commonKeys = listOf(
                    KeyEvent.KEYCODE_ENTER to "Enter",
                    KeyEvent.KEYCODE_SPACE to "Space",
                    KeyEvent.KEYCODE_DPAD_UP to "Up Arrow",
                    KeyEvent.KEYCODE_DPAD_DOWN to "Down Arrow",
                    KeyEvent.KEYCODE_DPAD_LEFT to "Left Arrow",
                    KeyEvent.KEYCODE_DPAD_RIGHT to "Right Arrow",
                    KeyEvent.KEYCODE_SHIFT_LEFT to "Left Shift",
                    KeyEvent.KEYCODE_CTRL_LEFT to "Left Ctrl",
                    KeyEvent.KEYCODE_ALT_LEFT to "Left Alt",
                    KeyEvent.KEYCODE_TAB to "Tab",
                    KeyEvent.KEYCODE_PAGE_UP to "Page Up",
                    KeyEvent.KEYCODE_PAGE_DOWN to "Page Down",
                    KeyEvent.KEYCODE_C to "C",
                    KeyEvent.KEYCODE_M to "M",
                    KeyEvent.KEYCODE_F to "F",
                    KeyEvent.KEYCODE_Z to "Z",
                    KeyEvent.KEYCODE_X to "X",
                    KeyEvent.KEYCODE_V to "V",
                    KeyEvent.KEYCODE_ESCAPE to "Esc"
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    commonKeys.forEach { (code, name) ->
                        val isSelected = detectedKeyCode == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberCyan.copy(alpha = 0.25f) else if (isDark) Color(0xFF1E2838) else Color(0xFFEDE4D8))
                                .border(1.dp, if (isSelected) CyberCyan else if (isDark) Color(0x33FFFFFF) else Color(0x22000000), RoundedCornerShape(8.dp))
                                .motionPress(onClick = { detectedKeyCode = code })
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyberCyan else textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Actions: Save / Clear / Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            detectedKeyCode = KeyEvent.KEYCODE_UNKNOWN
                        },
                        modifier = Modifier
                            .weight(1f)
                            .motionPress(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberRose)
                    ) {
                        Text("Unassign", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onKeySelected(detectedKeyCode)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .motionPress(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("Save Mapping", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
