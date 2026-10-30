package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val VxBackgroundLight = Color(0xFFF8F9FA)
val VxBackgroundDark = Color(0xFF0F172A)
val VxSurfaceDarkCard = Color(0xFF1E293B)
val VxNavyDark = Color(0xFF0F172A)
val VxBorderDark = Color(0xFF334155)
val VxTextPrimaryDark = Color(0xFFF8FAFC)
val VxTextSecondaryDark = Color(0xFF94A3B8)

// Top-level color constants for backwards compatibility with existing component references
val VxBorder = Color(0xFFE5E7EB)
val VxBorderBlue = Color(0xFFBFDBFE)
val VxBorderDanger = Color(0xFFFECACA)
val VxBorderGreen = Color(0xFFBBF7D0)
val VxBorderPurple = Color(0xFFE0E7FF)
val VxBorderSubtle = Color(0xFFE5E7EB)
val VxBorderWarning = Color(0xFFFDE68A)
val VxCyanAccent = Color(0xFF0284C7)
val VxDangerDark = Color(0xFFB91C1C)
val VxDangerRed = Color(0xFFDC2626)
val VxDeepBlue = Color(0xFF1D4ED8)
val VxEmerald = Color(0xFF16A34A)
val VxEmeraldAccent = Color(0xFF22C55E)
val VxEmeraldDark = Color(0xFF15803D)
val VxError = Color(0xFFDC2626)
val VxNavy = Color(0xFF1E293B)
val VxNavySurface = Color(0xFF1E293B)
val VxPrimaryAccent = Color(0xFF2563EB)
val VxPrimaryBlue = Color(0xFF2563EB)
val VxPurple = Color(0xFF6366F1)
val VxSlateBlue = Color(0xFF475569)
val VxSteelBlue = Color(0xFF334155)
val VxSuccess = Color(0xFF16A34A)
val VxSurfaceBlue = Color(0xFFEFF6FF)
val VxSurfaceCard = Color(0xFFFFFFFF)
val VxSurfaceDanger = Color(0xFFFEF2F2)
val VxSurfaceGreen = Color(0xFFF0FDF4)
val VxSurfaceLight = Color(0xFFFFFFFF)
val VxSurfacePurple = Color(0xFFEEF2FF)
val VxSurfaceSubtle = Color(0xFFF1F5F9)
val VxSurfaceWarning = Color(0xFFFFFBEB)
val VxTextMuted = Color(0xFF94A3B8)
val VxTextPrimary = Color(0xFF1E293B)
val VxTextSecondary = Color(0xFF64748B)
val VxWarningAmber = Color(0xFFD97706)
val VxWarningDark = Color(0xFFB45309)

object VxTheme {
    val surfaceCard: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val surfaceLight: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val surfaceSubtle: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val surfaceBlue: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E3A8A) else Color(0xFFEFF6FF)
    val surfaceBlueAccent: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFDBEAFE)
    val surfaceGreen: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFF0FDF4)
    val surfaceWarning: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF78350F) else Color(0xFFFFFBEB)
    val surfaceDanger: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF7F1D1D) else Color(0xFFFEF2F2)
    val surfacePurple: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF312E81) else Color(0xFFEEF2FF)
    val solarSurface: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF422006) else Color(0xFFFEF9C3)

    val border: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE5E7EB)
    val borderSubtle: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE5E7EB)
    val borderBlue: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E40AF) else Color(0xFFBFDBFE)
    val borderPurple: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3730A3) else Color(0xFFE0E7FF)
    val borderGreen: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF065F46) else Color(0xFFBBF7D0)
    val borderWarning: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF92400E) else Color(0xFFFDE68A)
    val borderDanger: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF991B1B) else Color(0xFFFECACA)

    val textPrimary: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val textSecondary: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF64748B)
    val textMuted: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF64748B) else Color(0xFF94A3B8)
    val textInverse: Color
        @Composable get() = Color(0xFFFFFFFF)

    val navy: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val navyDark: Color
        @Composable get() = Color(0xFF0F172A)
    val navySurface: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFF1E293B)
    val navyLight: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFF334155)

    val primaryAccent: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF6882F2) else Color(0xFF2563EB)
    val primaryBlue: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF6882F2) else Color(0xFF2563EB)
    val deepBlue: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3B82F6) else Color(0xFF1D4ED8)
    val lightBlue: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF60A5FA) else Color(0xFF3B82F6)
    val cyanAccent: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF38BDF8) else Color(0xFF0284C7)
    val purple: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF818CF8) else Color(0xFF6366F1)

    val emerald: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF16A34A)
    val emeraldAccent: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF10B981)
    val emeraldDark: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF6EE7B7) else Color(0xFF15803D)
    val warningAmber: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFBBF24) else Color(0xFFD97706)
    val warningDark: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFCD34D) else Color(0xFFB45309)
    val errorRed: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF87171) else Color(0xFFDC2626)
    val dangerDark: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
    val success: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF16A34A)
    val solarGold: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFDE047) else Color(0xFFEAB308)
}
