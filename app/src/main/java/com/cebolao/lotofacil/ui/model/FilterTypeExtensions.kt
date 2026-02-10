package com.cebolao.lotofacil.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.ShapeLine
import androidx.compose.ui.graphics.vector.ImageVector
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterType

val FilterType.titleRes: Int
    get() = when (this) {
        FilterType.SOMA_DEZENAS -> R.string.filter_soma_dezenas_title
        FilterType.PARES -> R.string.filter_pares_title
        FilterType.PRIMOS -> R.string.filter_primos_title
        FilterType.MOLDURA -> R.string.filter_moldura_title
        FilterType.RETRATO -> R.string.filter_retrato_title
        FilterType.FIBONACCI -> R.string.filter_fibonacci_title
        FilterType.MULTIPLOS_DE_3 -> R.string.filter_multiplos_3_title
        FilterType.REPETIDAS_CONCURSO_ANTERIOR -> R.string.filter_repetidas_anterior_title
    }

val FilterType.descriptionRes: Int
    get() = when (this) {
        FilterType.SOMA_DEZENAS -> R.string.filter_soma_dezenas_description
        FilterType.PARES -> R.string.filter_pares_description
        FilterType.PRIMOS -> R.string.filter_primos_description
        FilterType.MOLDURA -> R.string.filter_moldura_description
        FilterType.RETRATO -> R.string.filter_retrato_description
        FilterType.FIBONACCI -> R.string.filter_fibonacci_description
        FilterType.MULTIPLOS_DE_3 -> R.string.filter_multiplos_3_description
        FilterType.REPETIDAS_CONCURSO_ANTERIOR -> R.string.filter_repetidas_anterior_description
    }

val FilterType.icon: ImageVector
    get() = when (this) {
        FilterType.SOMA_DEZENAS -> Icons.Default.Calculate
        FilterType.PARES -> Icons.Default.Numbers
        FilterType.PRIMOS -> Icons.Default.Percent
        FilterType.MOLDURA -> Icons.Default.Grid4x4
        FilterType.RETRATO -> Icons.Outlined.ShapeLine
        FilterType.FIBONACCI -> Icons.Default.Timeline
        FilterType.MULTIPLOS_DE_3 -> Icons.Default.Functions
        FilterType.REPETIDAS_CONCURSO_ANTERIOR -> Icons.Default.Repeat
}
