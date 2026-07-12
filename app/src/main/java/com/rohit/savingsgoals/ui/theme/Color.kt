package com.rohit.savingsgoals.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand accents — same vivid orange in both themes for consistency.
val OrangeAccent = Color(0xFFFF6A3D)
val OrangeDeep = Color(0xFFE84E1B)
val SuccessGreen = Color(0xFF34C08C)
val DangerSoft = Color(0xFFE85D48)

// ---- Light palette ----
private val LightBackground = Color(0xFFFAF7F2)
private val LightCard = Color(0xFFFFFFFF)
private val LightPeachTint = Color(0xFFFCE7D8)
private val LightInk = Color(0xFF1C1C1C)
private val LightTextSecondary = Color(0xFF8D8A85)
private val LightTextTertiary = Color(0xFFBAB6AE)
private val LightTrack = Color(0xFFEFEBE3)
private val LightBorder = Color(0xFFEFEAE2)

// ---- Dark palette — warm charcoal-brown, not blue-black ----
private val DarkBackground = Color(0xFF16130F)
private val DarkCard = Color(0xFF241E17)
private val DarkPeachTint = Color(0xFF3E2C1B)
private val DarkInk = Color(0xFFF7F1E9)
private val DarkTextSecondary = Color(0xFFB8AC9C)
private val DarkTextTertiary = Color(0xFF8C8071)
private val DarkTrack = Color(0xFF2E2720)
private val DarkBorder = Color(0xFF3A3025)

// Semantic colors that automatically switch with system dark mode.
// Every screen references these by name — no per-screen changes needed.
val CreamBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkBackground else LightBackground

val CardWhite: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkCard else LightCard

val PeachTint: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkPeachTint else LightPeachTint

val InkBlack: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkInk else LightInk

val TextSecondary: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary

val TextTertiary: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkTextTertiary else LightTextTertiary

val TrackGray: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkTrack else LightTrack

val BorderFaint: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkBorder else LightBorder
