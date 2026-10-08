package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SaraDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = SaraCardGlow,
    onPrimaryContainer = LightViolet,
    secondary = NeonCyan,
    onSecondary = Color(0xFF003830),
    secondaryContainer = Color(0xFF132F2B),
    onSecondaryContainer = NeonCyan,
    tertiary = SoftPurple,
    onTertiary = Color.White,
    background = SaraBackground,
    onBackground = TextPrimary,
    surface = SaraSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SaraCardBg,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderSubtle,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun SaraTheme(
    darkTheme: Boolean = true, // SARA is strictly a futuristic dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SaraDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SaraTheme(darkTheme = true, content = content)
}
