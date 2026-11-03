package com.example

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.data.CursorStateHolder
import com.example.data.KeyCursorPreferences
import com.example.service.CursorAccessibilityService
import com.example.ui.components.GlassBottomNavigationBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HelpScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TestPadScreen
import com.example.ui.theme.KeyCursorTheme
import com.example.ui.theme.WarmObsidian

enum class AppScreen {
    DASHBOARD,
    SETTINGS,
    TEST_PAD,
    HELP
}

class MainActivity : ComponentActivity() {

    private lateinit var prefs: KeyCursorPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefs = KeyCursorPreferences(this)

        setContent {
            var isDarkTheme by remember { mutableStateOf(prefs.isDarkTheme) }

            KeyCursorTheme(darkTheme = isDarkTheme) {
                MainContent(
                    prefs = prefs,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = {
                        val next = !isDarkTheme
                        isDarkTheme = next
                        prefs.isDarkTheme = next
                    },
                    onThemeChanged = { newTheme ->
                        isDarkTheme = newTheme
                        prefs.isDarkTheme = newTheme
                    },
                    checkAccessibilityService = { isAccessibilityServiceEnabled() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isEnabled = isAccessibilityServiceEnabled()
        CursorStateHolder.setServiceRunning(isEnabled)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            if (service.resolveInfo.serviceInfo.packageName == packageName &&
                service.resolveInfo.serviceInfo.name == CursorAccessibilityService::class.java.name
            ) {
                return true
            }
        }
        return CursorAccessibilityService.instance != null
    }
}

@Composable
fun MainContent(
    prefs: KeyCursorPreferences,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onThemeChanged: (Boolean) -> Unit,
    checkAccessibilityService: () -> Boolean
) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    LaunchedEffect(Unit) {
        val running = checkAccessibilityService()
        CursorStateHolder.setServiceRunning(running)
    }

    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        currentScreen = AppScreen.DASHBOARD
    }

    val scaffoldBg = if (isDarkTheme) WarmObsidian else com.example.ui.theme.WarmLightBackground

    val backdropGradient = if (isDarkTheme) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF382316),
                Color(0xFF1E140E),
                WarmObsidian
            ),
            radius = 1200f
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF9F2),
                Color(0xFFF3ECE0),
                com.example.ui.theme.WarmLightBackground
            ),
            radius = 1200f
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = scaffoldBg
    ) { innerPadding ->
        // Warm Ambient Backdrop Gradient (Inspired by Reference UI)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backdropGradient)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.DASHBOARD -> DashboardScreen(
                        prefs = prefs,
                        onNavigateToSettings = { currentScreen = AppScreen.SETTINGS },
                        onNavigateToTestPad = { currentScreen = AppScreen.TEST_PAD },
                        onNavigateToHelp = { currentScreen = AppScreen.HELP },
                        onToggleTheme = onToggleTheme
                    )

                    AppScreen.SETTINGS -> SettingsScreen(
                        prefs = prefs,
                        onBack = { currentScreen = AppScreen.DASHBOARD },
                        onThemeChanged = onThemeChanged
                    )

                    AppScreen.TEST_PAD -> TestPadScreen(
                        prefs = prefs,
                        onBack = { currentScreen = AppScreen.DASHBOARD }
                    )

                    AppScreen.HELP -> HelpScreen(
                        onBack = { currentScreen = AppScreen.DASHBOARD }
                    )
                }
            }

            // Floating Glass Bottom Navigation Bar
            GlassBottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = { currentScreen = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
