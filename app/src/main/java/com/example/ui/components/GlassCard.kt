package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.animation.motionPress
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.WarmLightCardDarkBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 26.dp,
    borderColor: Color? = null,
    glowColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val shape = RoundedCornerShape(cornerRadius)

    val effectiveBorderColor = borderColor ?: if (isDark) GlassBorderSubtle else WarmLightCardDarkBorder

    val backgroundColors = if (isDark) {
        listOf(
            Color(0x3D2E241C),
            Color(0x281B1510)
        )
    } else {
        listOf(
            Color(0xF0FFFFFF),
            Color(0xDCF6EFE6)
        )
    }

    val shadowColor = if (isDark) glowColor else Color(0x18000000)

    val baseModifier = if (onClick != null) {
        modifier.motionPress(onClick = onClick)
    } else {
        modifier
    }

    Box(
        modifier = baseModifier
            .shadow(
                elevation = if (isDark) 12.dp else 6.dp,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = backgroundColors
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            effectiveBorderColor.copy(alpha = if (isDark) 0.45f else 0.35f),
                            effectiveBorderColor.copy(alpha = if (isDark) 0.12f else 0.15f)
                        )
                    )
                ),
                shape = shape
            )
            .padding(18.dp),
        content = content
    )
}
