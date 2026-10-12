package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6882F2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF232B47),
    onPrimaryContainer = Color(0xFFD4DCFA),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF132F23),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFF6882F2),
    background = VxBackgroundDark,
    surface = VxSurfaceDarkCard,
    surfaceVariant = Color(0xFF282C37),
    onBackground = VxTextPrimaryDark,
    onSurface = VxTextPrimaryDark,
    onSurfaceVariant = VxTextSecondaryDark,
    outline = VxBorderDark,
    error = VxError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = VxPrimaryAccent,
    onPrimary = Color.White,
    primaryContainer = VxSurfaceBlue,
    onPrimaryContainer = VxPrimaryAccent,
    secondary = VxSuccess,
    onSecondary = Color.White,
    secondaryContainer = VxSurfaceGreen,
    onSecondaryContainer = VxSuccess,
    tertiary = VxPrimaryAccent,
    background = VxBackgroundLight,
    surface = VxSurfaceLight,
    surfaceVariant = VxSurfaceSubtle,
    onBackground = VxTextPrimary,
    onSurface = VxTextPrimary,
    onSurfaceVariant = VxTextSecondary,
    outline = VxBorder,
    error = VxError,
    onError = Color.White
)

@Composable
fun VisionXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
