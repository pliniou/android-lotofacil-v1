package com.cebolao.lotofacil.domain.model

import com.cebolao.lotofacil.R

enum class FilterPreset(
    val titleRes: Int,
    val descriptionRes: Int
) {
    COMMON(
        titleRes = R.string.preset_common_title,
        descriptionRes = R.string.preset_common_desc
    ),
    GAUSS(
        titleRes = R.string.preset_gauss_title,
        descriptionRes = R.string.preset_gauss_desc
    ),
    EXTREMES(
        titleRes = R.string.preset_extremes_title,
        descriptionRes = R.string.preset_extremes_desc
    )
}
