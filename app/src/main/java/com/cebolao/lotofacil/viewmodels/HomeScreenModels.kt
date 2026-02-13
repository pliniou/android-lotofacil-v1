package com.cebolao.lotofacil.viewmodels

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.LooksTwo
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.domain.model.StatisticsReport

/**
 * Defines the different statistic patterns that can be visualised on the Home screen.
 * Each pattern has a user-visible title and an associated icon.
 */
@Stable
enum class StatisticPattern(@StringRes val titleRes: Int, val icon: ImageVector) {
    SUM(R.string.sum_label, Icons.Outlined.Functions),
    EVENS(R.string.even_label, Icons.Outlined.LooksTwo),
    FIBONACCI(R.string.fibonacci_label, Icons.Outlined.Timeline),
    MULTIPLES_OF_3(R.string.multiples_of_3_label, Icons.Outlined.FormatListNumbered)
}

@Stable
enum class DataLoadSource {
    CACHE,
    NETWORK,
    COMPUTED
}

@Stable
sealed interface HomeSyncState {
    data object Idle : HomeSyncState
    data class InProgress(val current: Int?, val total: Int?) : HomeSyncState
    data object Success : HomeSyncState
    data class Failed(val message: String?) : HomeSyncState
}

@Stable
data class NextDrawUiModel(
    val contestNumber: Int,
    val date: String?,
    val prizeEstimate: Double,
    val isAccumulated: Boolean
)

/**
 * Holds all state required by the Home screen, including loading and error flags, the
 * last draw statistics, computed summary statistics, and UI selections such as the
 * currently selected pattern and time window.
 */
@Stable
data class HomeUiState(
    val isScreenLoading: Boolean = true,
    val isStatsLoading: Boolean = false,
    @StringRes val errorMessageResId: Int? = null,
    val lastDrawStats: LastDrawStats? = null,
    val statistics: StatisticsReport? = null,
    val selectedPattern: StatisticPattern = StatisticPattern.SUM,
    val selectedTimeWindow: Int = 0,
    val historySource: DataLoadSource = DataLoadSource.CACHE,
    val statisticsSource: DataLoadSource = DataLoadSource.CACHE,
    val isShowingStaleData: Boolean = false,
    val lastUpdateTime: String? = null,
    val nextDraw: NextDrawUiModel? = null,
    val isTodayDrawDay: Boolean = false,
    val syncState: HomeSyncState = HomeSyncState.Idle
)
