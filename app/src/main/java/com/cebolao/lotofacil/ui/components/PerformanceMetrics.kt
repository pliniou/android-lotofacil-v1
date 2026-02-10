package com.cebolao.lotofacil.ui.components

import android.app.ActivityManager
import android.os.Debug
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing
import java.util.Locale
import kotlinx.coroutines.delay

data class PerformanceMetrics(
    val memoryUsage: Long = 0,
    val memoryAvailable: Long = 0,
    val nativeMemory: Long = 0,
    val frameTime: Float = 0f,
    val recompositionCount: Int = 0
)

@Composable
fun PerformanceMetricsOverlay(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var metrics by remember { mutableStateOf(PerformanceMetrics()) }
    var updateCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)

            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory() / 1_048_576
            val freeMemory = runtime.freeMemory() / 1_048_576
            val usedMemory = totalMemory - freeMemory

            val activityManager = context.getSystemService(ActivityManager::class.java)
            val memInfo = ActivityManager.MemoryInfo()
            activityManager?.getMemoryInfo(memInfo)
            val availableMemory = memInfo.availMem / 1_048_576

            val nativeMemory = Debug.getNativeHeapAllocatedSize() / 1_048_576

            metrics = PerformanceMetrics(
                memoryUsage = usedMemory,
                memoryAvailable = availableMemory,
                nativeMemory = nativeMemory,
                frameTime = 16.67f,
                recompositionCount = updateCount
            )
            updateCount++
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                shape = MaterialTheme.shapes.medium
            ),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.performance_metrics_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.width(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.performance_metrics_close),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            MetricCard(
                title = stringResource(id = R.string.performance_metrics_memory_title),
                metrics = listOf(
                    stringResource(id = R.string.performance_metrics_metric_used) to "${metrics.memoryUsage} MB",
                    stringResource(id = R.string.performance_metrics_metric_available) to "${metrics.memoryAvailable} MB",
                    stringResource(id = R.string.performance_metrics_metric_native) to "${metrics.nativeMemory} KB"
                )
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            MetricCard(
                title = stringResource(id = R.string.performance_metrics_performance_title),
                metrics = listOf(
                    stringResource(id = R.string.performance_metrics_metric_frame_time) to
                        "${String.format(Locale.getDefault(), "%.2f", metrics.frameTime)} ms",
                    stringResource(id = R.string.performance_metrics_metric_recompositions) to
                        "${metrics.recompositionCount}",
                    stringResource(id = R.string.performance_metrics_metric_status) to if (metrics.frameTime <= 16.67f) {
                        stringResource(id = R.string.performance_metrics_status_smooth)
                    } else {
                        stringResource(id = R.string.performance_metrics_status_jank)
                    }
                )
            )

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            Text(
                text = stringResource(id = R.string.performance_metrics_update_interval),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.xs)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    metrics: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = AppSpacing.sm)
            )

            metrics.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceMetricsButton(
    modifier: Modifier = Modifier,
    isDebugBuild: Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }

    if (!isDebugBuild) return

    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            contentAlignment = Alignment.TopEnd
        ) {
            PerformanceMetricsOverlay(
                modifier = Modifier.fillMaxWidth(0.95f),
                onDismiss = { isVisible = false }
            )
        }
    } else {
        IconButton(
            onClick = { isVisible = true },
            modifier = modifier
                .padding(AppSpacing.md)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Text(
                text = stringResource(id = R.string.performance_metrics_toggle),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
