package com.cookbook.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    primaryContainer = GreenLight,
    secondary = CreamBackground,
    onSecondary = DarkGray,
    surface = White,
    onSurface = DarkGray,
    background = CreamLight,
    onBackground = DarkGray,
    error = ErrorRed,
    onError = White
)

@Composable
fun AICookBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
