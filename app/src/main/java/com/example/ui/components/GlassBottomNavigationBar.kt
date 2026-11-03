package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.AppScreen
import com.example.ui.animation.MotionSprings
import com.example.ui.animation.motionPress
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.TextWarmLightMuted
import com.example.ui.theme.TextWarmMuted
import com.example.ui.theme.TextWarmPrimary
import com.example.ui.theme.WarmAccentAmber
import com.example.ui.theme.WarmAccentGlow

/**
 * Floating Frosted Glass Bottom Navigation Bar.
 * Replicates the elegant glassmorphic floating bar from the reference design,
 * with illuminated pill active indicator and fluid spring response.
 */
@Composable
fun GlassBottomNavigationBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val barBackground = if (isDark) {
        listOf(
            Color(0x402C221A),
            Color(0x3519130E)
        )
    } else {
        listOf(
            Color(0xF0FFFFFF),
            Color(0xDCF2ECE3)
        )
    }

    val barBorder = if (isDark) {
        listOf(
            Color(0x45FFFFFF),
            Color(0x18FFFFFF)
        )
    } else {
        listOf(
            Color(0x33000000),
            Color(0x12000000)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    if (isDark) 16.dp else 8.dp,
                    RoundedCornerShape(32.dp),
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x22000000)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Brush.verticalGradient(colors = barBackground))
                .border(
                    1.dp,
                    Brush.verticalGradient(colors = barBorder),
                    RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Dashboard",
                isSelected = currentScreen == AppScreen.DASHBOARD,
                onClick = { onNavigate(AppScreen.DASHBOARD) },
                testTag = "bottom_nav_home"
            )

            NavItem(
                icon = Icons.Default.Mouse,
                label = "Mouse",
                isSelected = currentScreen == AppScreen.SETTINGS,
                onClick = { onNavigate(AppScreen.SETTINGS) },
                testTag = "bottom_nav_settings"
            )

            NavItem(
                icon = Icons.Default.Build,
                label = "Test Pad",
                isSelected = currentScreen == AppScreen.TEST_PAD,
                onClick = { onNavigate(AppScreen.TEST_PAD) },
                testTag = "bottom_nav_test"
            )

            NavItem(
                icon = Icons.Default.HelpOutline,
                label = "Help",
                isSelected = currentScreen == AppScreen.HELP,
                onClick = { onNavigate(AppScreen.HELP) },
                testTag = "bottom_nav_help"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val isDark = LocalIsDarkTheme.current
    val unselectedTint = if (isDark) TextWarmMuted else TextWarmLightMuted
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) WarmAccentAmber else unselectedTint,
        animationSpec = androidx.compose.animation.core.tween(250),
        label = "NavIconTint"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color(0x38E28743) else Color.Transparent
            )
            .border(
                1.dp,
                if (isSelected) Color(0x55E28743) else Color.Transparent,
                CircleShape
            )
            .motionPress(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}
