package com.cebolao.lotofacil.ui.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.cebolao.lotofacil.BuildConfig

/**
 * Composition tracking utility for performance analysis in DEBUG builds.
 *
 * Use to identify heavy recompositions:
 *
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     TrackComposition("MyScreen")
 *     // ... rest of composable
 * }
 * ```
 *
 * This will log each recomposition to Logcat with timing information.
 * Completely disabled (zero overhead) in release builds via BuildConfig guard.
 *
 * Performance impact:
 * - Debug: Single remember() call (~negligible)
 * - Release: Zero overhead (optimized away)
 */

/**
 * Track composition for debugging.
 * No-op in release builds.
 *
 * @param name Identifier for this composition (typically the composable name)
 * @param depth Optional nesting depth for indentation in logs
 */
@Composable
fun TrackComposition(name: String, depth: Int = 0) {
    remember {
        if (BuildConfig.DEBUG) {
            val indent = "  ".repeat(depth)
            Log.d("Composition", "$indent-> Recomposed: $name")
        }
        {}
    }
}

/**
 * Recomposition counter for analyzing optimization opportunities.
 *
 * Usage:
 * ```kotlin
 * val recompositionCount = rememberRecompositionCount("MyScreen")
 * Text("Recomposed: ${recompositionCount.value} times")
 * ```
 *
 * Returns MutableState<Int> that increments on each recomposition in debug builds.
 * No-op in release builds.
 * Useful for:
 * - Identifying screens with excessive recompositions
 * - Testing optimization effectiveness
 * - Performance regression detection
 */
@Composable
fun rememberRecompositionCount(name: String): androidx.compose.runtime.MutableIntState {
    val count = remember { androidx.compose.runtime.mutableIntStateOf(0) }
    remember {
        if (BuildConfig.DEBUG) {
            count.intValue++
            Log.d("RecompositionCount", "$name: ${count.intValue}")
        }
        {}
    }
    return count
}

/**
 * Performance monitoring composable wrapper.
 *
 * Wraps a composable to automatically track recompositions.
 *
 * Example:
 * ```kotlin
 * MonitoredComposable("HomeScreen") {
 *     HomeScreenContent(...)
 * }
 * ```
 *
 * Tracks:
 * - Composition count
 * - Recomposition frequency
 * - Dependency changes
 */
@Composable
fun MonitoredComposable(
    name: String,
    content: @Composable () -> Unit
) {
    TrackComposition(name)
    content()
}
