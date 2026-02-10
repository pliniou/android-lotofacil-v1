package com.cebolao.lotofacil.core.utils

import android.util.Log
import androidx.compose.runtime.Composable
import com.cebolao.lotofacil.BuildConfig

/**
 * Composition performance metrics snapshot.
 */
data class CompositionMetrics(
    val compositionCount: Int = 0,
    val averageCompositionTime: Long = 0,
    val peakCompositionTime: Long = 0
)

private const val COMPOSITION_LOG_TAG = "CompositionPerf"
private const val DEFAULT_JANK_THRESHOLD_MS = 16L

/**
 * Measures the execution time of a composable block for the current composition pass.
 *
 * Usage:
 * ```kotlin
 * @Composable
 * fun HomeScreen() {
 *     measureComposition("HomeScreen") {
 *         HomeScreenContent()
 *     }
 * }
 * ```
 *
 * Monitoring strategy:
 * 1. Keep this enabled in debug builds while profiling expensive screens.
 * 2. Use Macrobenchmark/Baseline Profile for release-grade performance validation.
 * 3. Promote recurring thresholds/events to your observability pipeline when needed.
 */
@Composable
inline fun <T> measureComposition(
    name: String,
    crossinline block: @Composable () -> T
): T {
    if (!BuildConfig.DEBUG) return block()

    val startTimeNs = System.nanoTime()
    val result = block()
    val durationMs = (System.nanoTime() - startTimeNs) / 1_000_000

    logCompositionTime(name, durationMs)
    return result
}

/**
 * Logs composition timing information in debug builds only.
 */
fun logCompositionTime(name: String, timeMs: Long, thresholdMs: Long = DEFAULT_JANK_THRESHOLD_MS) {
    if (!BuildConfig.DEBUG) return

    val status = if (timeMs >= thresholdMs) "SLOW" else "OK"
    Log.d(
        COMPOSITION_LOG_TAG,
        "[$status] Composition [$name] took ${timeMs}ms (threshold=${thresholdMs}ms)"
    )
}

/**
 * Memory usage extension.
 */
fun getMemoryUsagePercent(): Float {
    val runtime = Runtime.getRuntime()
    val maxMemory = runtime.maxMemory().toFloat()
    val totalMemory = runtime.totalMemory().toFloat()
    val freeMemory = runtime.freeMemory().toFloat()
    val usedMemory = totalMemory - freeMemory

    return (usedMemory / maxMemory) * 100f
}

/**
 * Check if memory usage is critical.
 */
fun isMemoryCritical(threshold: Float = 85f): Boolean {
    return getMemoryUsagePercent() >= threshold
}
