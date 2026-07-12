package com.rohit.savingsgoals.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightScheme = lightColorScheme(
    primary = OrangeAccent,
    onPrimary = Color.White,
    secondary = SuccessGreen,
    tertiary = OrangeDeep,
    background = Color(0xFFFAF7F2),
    surface = Color.White,
    surfaceVariant = Color(0xFFFCE7D8),
    onBackground = Color(0xFF1C1C1C),
    onSurface = Color(0xFF1C1C1C),
    onSurfaceVariant = Color(0xFF8D8A85),
    error = DangerSoft
)

private val DarkScheme = darkColorScheme(
    primary = OrangeAccent,
    onPrimary = Color(0xFF16130F),
    secondary = SuccessGreen,
    tertiary = OrangeDeep,
    background = Color(0xFF16130F),
    surface = Color(0xFF241E17),
    surfaceVariant = Color(0xFF3E2C1B),
    onBackground = Color(0xFFF7F1E9),
    onSurface = Color(0xFFF7F1E9),
    onSurfaceVariant = Color(0xFFB8AC9C),
    error = DangerSoft
)

// Tabular figures keep the currency numbers steady, ledger-like, as they change.
private val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        letterSpacing = (-0.6).sp,
        fontFeatureSettings = "tnum"
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        letterSpacing = (-0.4).sp
    ),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    )
)

@Composable
fun SavingsGoalsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkScheme else LightScheme,
        typography = AppTypography,
        content = content
    )
}

/**
 * The main ambient backdrop — a warm glow that starts strong at the top and
 * settles into the base tone. Deliberately high-contrast stops so it reads
 * clearly instead of blending into near-invisible off-whites.
 */
@Composable
fun ambientBackground(): Brush {
    return if (isSystemInDarkTheme()) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF43290F), // warm ember glow
                Color(0xFF241A10),
                Color(0xFF16130F)  // base
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFD3A0), // bright apricot glow
                Color(0xFFFCEADA),
                Color(0xFFFAF7F2)  // base
            )
        )
    }
}

/**
 * Soft circular highlight layered near the top of the screen for extra depth —
 * paired with [ambientBackground] behind it.
 */
@Composable
fun ambientGlow(): Brush {
    val glowColor = if (isSystemInDarkTheme()) Color(0xFFFF9A52) else Color(0xFFFF8A4C)
    return Brush.radialGradient(
        colors = listOf(glowColor.copy(alpha = 0.38f), Color.Transparent)
    )
}
