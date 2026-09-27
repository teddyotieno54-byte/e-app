package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BizlyPrimary,
    onPrimary = Color.White,
    primaryContainer = BizlyPrimaryContainer,
    onPrimaryContainer = BizlyPrimary,
    secondary = BizlyPrimaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF1E1B4B),
    tertiary = BizlySuccess,
    onTertiary = Color.White,
    tertiaryContainer = BizlySuccessLight,
    onTertiaryContainer = BizlySuccessDark,
    background = BizlyBackground,
    onBackground = BizlyTextPrimary,
    surface = BizlySurface,
    onSurface = BizlyTextPrimary,
    surfaceVariant = BizlySurfaceSubtle,
    onSurfaceVariant = BizlyTextSecondary,
    outline = BizlyBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = BizlyDanger,
    onError = Color.White,
    errorContainer = BizlyDangerLight,
    onErrorContainer = BizlyDanger
)

private val DarkColorScheme = darkColorScheme(
    primary = BizlyPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = BizlyNavy,
    onPrimaryContainer = Color(0xFFBFDBFE),
    secondary = Color(0xFF60A5FA),
    onSecondary = Color(0xFF0F172A),
    tertiary = BizlySuccess,
    onTertiary = Color.White,
    background = BizlyDarkBackground,
    onBackground = BizlyDarkTextPrimary,
    surface = BizlyDarkSurface,
    onSurface = BizlyDarkTextPrimary,
    surfaceVariant = BizlyDarkSurfaceVariant,
    onSurfaceVariant = BizlyDarkTextSecondary,
    outline = Color(0xFF334155),
    error = BizlyDanger,
    onError = Color.White
)

@Composable
fun BizlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Bizly uses strict brand styling to maintain consistent SaaS polish
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
