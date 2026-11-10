package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = NavySurfaceVariant,
    onPrimaryContainer = NeonVioletLight,
    secondary = NeonViolet,
    onSecondary = Color.White,
    secondaryContainer = NavyCard,
    onSecondaryContainer = Color.White,
    tertiary = CyanAccent,
    background = NavyDark,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = DangerRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = ElectricBlueDark,
    secondary = NeonViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = NeonViolet,
    tertiary = CyanAccent,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = DangerRose,
    onError = Color.White
)

@Composable
fun BuildlabsTheme(
    darkTheme: Boolean = true, // Default to Buildlabs signature dark aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
