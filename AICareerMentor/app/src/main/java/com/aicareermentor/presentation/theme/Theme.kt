package com.aicareermentor.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Brand Colors
val Brand50  = Color(0xFFEEF2FF)
val Brand100 = Color(0xFFE0E7FF)
val Brand400 = Color(0xFF818CF8)
val Brand500 = Color(0xFF6366F1)
val Brand600 = Color(0xFF4F46E5)
val Brand700 = Color(0xFF4338CA)
val Brand900 = Color(0xFF312E81)

val Violet400 = Color(0xFFA78BFA)
val Violet500 = Color(0xFF8B5CF6)
val Violet600 = Color(0xFF7C3AED)
val Pink500   = Color(0xFFEC4899)

// Semantic
val GradientStart = Brand500
val GradientMid   = Violet500
val GradientEnd   = Pink500

val ScoreGreen = Color(0xFF22C55E)
val ScoreAmber = Color(0xFFF59E0B)
val ScoreRed   = Color(0xFFEF4444)

val SurfaceDark  = Color(0xFF1A1625)
val BgDark       = Color(0xFF0F0D1A)

private val DarkColors = darkColorScheme(
    primary            = Brand400,
    onPrimary          = Brand900,
    primaryContainer   = Brand700,
    onPrimaryContainer = Brand100,
    secondary          = Violet400,
    onSecondary        = Color(0xFF1E0060),
    secondaryContainer = Color(0xFF33006B),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary           = Pink500,
    onTertiary         = Color.White,
    background         = BgDark,
    onBackground       = Color(0xFFEAE0FF),
    surface            = SurfaceDark,
    onSurface          = Color(0xFFEAE0FF),
    surfaceVariant     = Color(0xFF2A2440),
    onSurfaceVariant   = Color(0xFFCAC4D0),
    outline            = Color(0xFF6B6478),
    error              = Color(0xFFCF6679),
    errorContainer     = Color(0xFF552023),
    onErrorContainer   = Color(0xFFF2B8BB)
)

private val LightColors = lightColorScheme(
    primary            = Brand600,
    onPrimary          = Color.White,
    primaryContainer   = Brand100,
    onPrimaryContainer = Brand900,
    secondary          = Violet600,
    onSecondary        = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF3B0764),
    tertiary           = Color(0xFFDB2777),
    onTertiary         = Color.White,
    background         = Color(0xFFF8F7FF),
    onBackground       = Color(0xFF1C1B1F),
    surface            = Color.White,
    onSurface          = Color(0xFF1C1B1F),
    surfaceVariant     = Color(0xFFEDE7F6),
    onSurfaceVariant   = Color(0xFF49454F),
    outline            = Color(0xFF79747E),
    error              = Color(0xFFB91C1C),
    errorContainer     = Color(0xFFFEE2E2),
    onErrorContainer   = Color(0xFF7F1D1D)
)

@Composable
fun AICareerMentorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
