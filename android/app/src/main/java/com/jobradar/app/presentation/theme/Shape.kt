package com.jobradar.app.presentation.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

/**
 * Corner radius tokens (design spec §2.4).
 */
val JobRadarShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),    // radius-sm
    small = RoundedCornerShape(12.dp),        // radius-md
    medium = RoundedCornerShape(16.dp),       // radius-lg
    large = RoundedCornerShape(20.dp),        // radius-xl
    extraLarge = RoundedCornerShape(28.dp),
)

/** spacing tokens — 4pt grid. */
object JobRadarDp {
    val Space4 = 4.dp
    val Space8 = 8.dp
    val Space12 = 12.dp
    val Space16 = 16.dp
    val Space20 = 20.dp
    val Space24 = 24.dp
    val Space32 = 32.dp
    val Space40 = 40.dp
    val Space48 = 48.dp
    val Space64 = 64.dp

    // Motion durations (design spec §2.6). Pixel-tuned on device.
    val DurationFastMs = 120
    val DurationBaseMs = 240
    val DurationSlowMs = 400
    val DurationRadarMs = 1600
}

/**
 * Motion tokens (design spec §2.6) — the single source of truth for every
 * animation in the app. Tuned on device to give the "深渊看板" premium feel with
 * a restrained ("君子内敛") settle: springs use damping ≈0.55 and a stiffness
 * that bounces just enough to feel alive without being janky.
 */
object JobRadarMotion {

    // --- Springs (the card / button / tab physics) ---
    /** Damping ratio ~0.55 (MediumBouncy) — the signature "bouncy" of the product. */
    const val Damping = 0.55f

    /** Stiffness tuned so bounces settle in ~2-3 oscillations, not endless. */
    val Stiffness = Spring.StiffnessMediumLow

    /** A card-level spring: gentle, forward-press feel. */
    fun cardSpring() = spring<Float>(dampingRatio = Damping, stiffness = Stiffness)

    /** A subtle spring for small elements (tabs, chips). */
    fun microSpring() = spring<Float>(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)

    /** A tighter spring for emphasis moments (detection pop). */
    fun popSpring() = spring<Float>(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium)

    // --- Easing curves ---
    /** Default entrance: fast-out, gentle settle (design spec ease-out). */
    val EaseOut = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Standard intermediate transition. */
    val EaseInOut = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

    /** A softer deceleration for large surfaces (detail hero). */
    val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)

    // --- Builders ---
    fun entrance(duration: Int = JobRadarDp.DurationBaseMs) = tween<Float>(duration, easing = EaseOut)
    fun emphase(duration: Int = JobRadarDp.DurationFastMs) = tween<Float>(duration, easing = EaseOut)
}
