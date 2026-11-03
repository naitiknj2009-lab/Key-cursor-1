package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkTheme = staticCompositionLocalOf { true }

val WarmGlassColorScheme = darkColorScheme(
    primary = WarmAccentAmber,
    onPrimary = Color(0xFF140E0A),
    primaryContainer = WarmSurfaceVariant,
    onPrimaryContainer = WarmAccentGlow,
    secondary = WarmGold,
    onSecondary = Color(0xFF1C130D),
    secondaryContainer = Color(0xFF332317),
    onSecondaryContainer = WarmPillActive,
    tertiary = WarmEmerald,
    onTertiary = Color.Black,
    background = WarmObsidian,
    onBackground = TextWarmPrimary,
    surface = WarmSurface,
    onSurface = TextWarmPrimary,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = TextWarmSecondary,
    outline = GlassBorder,
    error = WarmRose,
    onError = Color.White
)

val WarmLightColorScheme = lightColorScheme(
    primary = WarmAccentAmber,
    onPrimary = Color.White,
    primaryContainer = WarmLightSurfaceVariant,
    onPrimaryContainer = Color(0xFF5A2A08),
    secondary = WarmTerracotta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0E5D8),
    onSecondaryContainer = Color(0xFF3D1F08),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = WarmLightBackground,
    onBackground = TextWarmLightPrimary,
    surface = WarmLightSurface,
    onSurface = TextWarmLightPrimary,
    surfaceVariant = WarmLightSurfaceVariant,
    onSurfaceVariant = TextWarmLightSecondary,
    outline = WarmLightCardDarkBorder,
    error = Color(0xFFDC2626),
    onError = Color.White
)

object KeyCursorColors {
    val textPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalIsDarkTheme.current) TextWarmPrimary else TextWarmLightPrimary

    val textSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalIsDarkTheme.current) TextWarmSecondary else TextWarmLightSecondary

    val textMuted: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalIsDarkTheme.current) TextWarmMuted else TextWarmLightMuted

    val cardBorder: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalIsDarkTheme.current) GlassBorderSubtle else WarmLightCardDarkBorder
}

@Composable
fun KeyCursorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WarmGlassColorScheme else WarmLightColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

