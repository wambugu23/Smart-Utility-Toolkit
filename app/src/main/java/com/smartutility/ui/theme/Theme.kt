package com.smartutility.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepNavy      = Color(0xFF0A0E1A)
val SurfaceNavy   = Color(0xFF111827)
val CardNavy      = Color(0xFF1A2236)
val BorderNavy    = Color(0xFF2A3450)
val AccentCyan    = Color(0xFF00D4FF)
val AccentMint    = Color(0xFF00FFA3)
val AccentAmber   = Color(0xFFFFB800)
val TextPrimary   = Color(0xFFF0F4FF)
val TextSecondary = Color(0xFF8A99B8)
val TextMuted     = Color(0xFF4A5568)

private val DarkColorScheme = darkColorScheme(
    primary        = AccentCyan,
    secondary      = AccentMint,
    tertiary       = AccentAmber,
    background     = DeepNavy,
    surface        = SurfaceNavy,
    surfaceVariant = CardNavy,
    onPrimary      = DeepNavy,
    onSecondary    = DeepNavy,
    onBackground   = TextPrimary,
    onSurface      = TextPrimary,
    outline        = BorderNavy
)

@Composable
fun SmartUtilityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content     = content
    )
}