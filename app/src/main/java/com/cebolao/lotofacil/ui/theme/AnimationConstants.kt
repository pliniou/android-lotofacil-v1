package com.cebolao.lotofacil.ui.theme

import androidx.compose.runtime.Immutable

/**
 * Centralized animation constants and specs
 * Ensures consistency across all Compose animations
 * 
 * All duration values in milliseconds
 */
@Immutable
data class AppMotion(
    val durationShortMs: Int = 100,
    val durationMediumMs: Int = 200,
    val durationLongMs: Int = 400,
    val durationNumberBallMs: Int = 300,
    val durationElevationMs: Int = 150,
    val durationColorChangeMs: Int = 300,
    val durationFadeMs: Int = 250,
    val durationScaleMs: Int = 180,
    val durationFilterPanelMs: Int = 200,
    val durationProbabilityMs: Int = 300,
    val durationScoreCountMs: Int = 250,
    val durationShimmerMs: Int = 1200,
    val delayEntryMs: Long = 100L,
    val delayCheckerMs: Long = 150L,
    val delayFiltersMs: Long = 120L,
    val delayStaggerMs: Long = 60L,
    val maxStaggerDelayMs: Long = 500L,
    val splashMinDurationMs: Long = 1300L
)

val DefaultAppMotion = AppMotion()

object AppAnimationConstants {
    
    // Duration timings (milliseconds)
    object Durations {
        // List item animations, card transitions
        const val Medium = 300
        
        // Screen transitions, complex sequences
        const val Long = 500
    }

    // Delay constants for staggered animations
    object Delays {
        const val Minimal = 50
        const val Short = 100
        const val Medium = 150
        const val Long = 200
    }
}
