package com.jobradar.app.presentation.theme

import androidx.compose.ui.graphics.Color

// Dark palette — the default & primary theme (see 设计系统规范 §2.1).
object JobRadarColors {
    val Background = Color(0xFF0A0E17)
    val BackgroundSecondary = Color(0xFF111827)
    val Surface = Color(0xFF151B29)
    val SurfaceElevated = Color(0xFF1B2333)
    val SurfaceGlass = Color(0x0FFFFFFF)          // rgba(255,255,255,0.06)
    val SurfaceGlassStrong = Color(0x1AFFFFFF)     // rgba(255,255,255,0.10)
    val Primary = Color(0xFF2DE1C2)
    val PrimaryDark = Color(0xFF17A890)
    val Accent = Color(0xFF6C7CFF)
    val AccentSoft = Color(0xFF8B96FF)
    val Success = Color(0xFF22D07E)
    val Warning = Color(0xFFFFB020)
    val Danger = Color(0xFFFF5A6A)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFF8A94A6)
    val TextTertiary = Color(0xFF5A6474)
    val Border = Color(0x14FFFFFF)                // rgba(255,255,255,0.08)
    val Overlay = Color(0x99000000)                // rgba(0,0,0,0.6)
}

// Linear / radial gradients used across the UI (design spec §2.1).
object JobRadarGradients {
    val Primary = listOf(JobRadarColors.Primary, JobRadarColors.PrimaryDark)         // grad-primary
    val Glow = listOf(JobRadarColors.Accent, JobRadarColors.Primary)                 // grad-glow
    val Card = listOf(JobRadarColors.SurfaceElevated, Color(0xFF121826))             // grad-card
    val BackgroundRadial = listOf(Color(0xFF0A0E17), Color(0xFF131C2B))              // radial-primary
}

// Glow colors (design spec §2.5).
object JobRadarGlow {
    // rgba(45,225,194,0.45)
    val Primary = Color(red = 0.18f, green = 0.88f, blue = 0.76f, alpha = 0.45f)
    // rgba(108,124,255,0.40)
    val Accent = Color(red = 0.42f, green = 0.49f, blue = 1f, alpha = 0.40f)
}
