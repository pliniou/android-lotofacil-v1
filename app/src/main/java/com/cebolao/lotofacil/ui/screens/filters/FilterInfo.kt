package com.cebolao.lotofacil.ui.screens.filters

import com.cebolao.lotofacil.domain.model.FilterType

data class FilterInfo(
    val id: FilterType,
    val title: String,
    val description: String
)
