package com.jobradar.app.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Maps our design tokens onto a Material3 [androidx.compose.material3.ColorScheme]. */
private val DarkScheme = darkColorScheme(
    primary = JobRadarColors.Primary,
    onPrimary = Color(0xFF00201B),
    primaryContainer = JobRadarColors.PrimaryDark,
    onPrimaryContainer = Color(0xFF00201B),
    secondary = JobRadarColors.Accent,
    onSecondary = Color(0xFF0A0E17),
    background = JobRadarColors.Background,
    onBackground = JobRadarColors.TextPrimary,
    surface = JobRadarColors.Surface,
    onSurface = JobRadarColors.TextPrimary,
    surfaceVariant = JobRadarColors.SurfaceElevated,
    onSurfaceVariant = JobRadarColors.TextSecondary,
    outline = JobRadarColors.Border,
    error = JobRadarColors.Danger,
    onError = Color(0xFFFFFFFF),
)

private val LightScheme = lightColorScheme(
    primary = JobRadarColors.PrimaryDark,
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F6FA),
    onBackground = Color(0xFF0A0E17),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0A0E17),
)

/**
 * App theme. Dark is primary; light is an optional secondary which maps the
 * same tokens so animations/color semantics stay identical.
 */
@androidx.compose.runtime.Composable
fun JobRadarTheme(
    darkTheme: Boolean = true,
    content: @androidx.compose.runtime.Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkScheme else LightScheme
    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = JobRadarTypography,
        shapes = JobRadarShapes,
        content = content,
    )
}
