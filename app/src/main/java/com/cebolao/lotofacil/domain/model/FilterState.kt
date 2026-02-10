package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/** Categorizes the restrictiveness of a filter based on its range size. */
enum class RestrictivenessCategory {
    DISABLED,
    VERY_LOOSE,
    LOOSE,
    MODERATE,
    TIGHT,
    VERY_TIGHT
}

@Immutable
@Serializable
data class FilterState(
    val type: FilterType,
    val isEnabled: Boolean = false,
    val selectedRange: ClosedFloatingPointRange<Float> = type.defaultRange
) {
    val rangePercentage: Float by lazy {
        if (!isEnabled) return@lazy 0f
        val totalRange = type.fullRange.endInclusive - type.fullRange.start
        if (totalRange > 0) (selectedRange.endInclusive - selectedRange.start) / totalRange else 0f
    }

    val restrictivenessCategory: RestrictivenessCategory by lazy {
        when {
            !isEnabled -> RestrictivenessCategory.DISABLED
            rangePercentage >= 0.8f -> RestrictivenessCategory.VERY_LOOSE
            rangePercentage >= 0.6f -> RestrictivenessCategory.LOOSE
            rangePercentage >= 0.4f -> RestrictivenessCategory.MODERATE
            rangePercentage >= 0.2f -> RestrictivenessCategory.TIGHT
            else -> RestrictivenessCategory.VERY_TIGHT
        }
    }

    fun containsValue(value: Int): Boolean {
        return if (isEnabled) value.toFloat() in selectedRange else true
    }
}
