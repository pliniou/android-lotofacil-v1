package com.cebolao.lotofacil.navigation

import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.model.LotofacilGame

fun LotofacilGame.toGeneratorPreset(): String = numbers.toGeneratorPreset()

fun Set<Int>.toGeneratorPreset(): String = sorted().joinToString(separator = ",")

fun String.toPresetNumbersOrNull(): Set<Int>? {
    val parsed = split(',')
        .mapNotNull { token -> token.trim().toIntOrNull() }
        .filter { number -> number in LotofacilConstants.VALID_NUMBER_RANGE }
        .toSet()

    return parsed.takeIf { it.size == LotofacilConstants.GAME_SIZE }
}
