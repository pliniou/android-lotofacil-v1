package com.cebolao.lotofacil.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FilterType(
    val fullRange: ClosedFloatingPointRange<Float>,
    val defaultRange: ClosedFloatingPointRange<Float>,
    val historicalSuccessRate: Float
) {
    SOMA_DEZENAS(
        fullRange = 120f..270f,
        defaultRange = 170f..220f,
        historicalSuccessRate = 0.72f
    ),
    PARES(
        fullRange = 0f..12f,
        defaultRange = 6f..9f,
        historicalSuccessRate = 0.78f
    ),
    PRIMOS(
        fullRange = 0f..9f,
        defaultRange = 4f..7f,
        historicalSuccessRate = 0.74f
    ),
    MOLDURA(
        fullRange = 0f..15f,
        defaultRange = 8f..11f,
        historicalSuccessRate = 0.76f
    ),
    RETRATO(
        fullRange = 0f..9f,
        defaultRange = 4f..7f,
        historicalSuccessRate = 0.71f
    ),
    FIBONACCI(
        fullRange = 0f..7f,
        defaultRange = 3f..5f,
        historicalSuccessRate = 0.68f
    ),
    MULTIPLOS_DE_3(
        fullRange = 0f..8f,
        defaultRange = 3f..6f,
        historicalSuccessRate = 0.69f
    ),
    REPETIDAS_CONCURSO_ANTERIOR(
        fullRange = 0f..15f,
        defaultRange = 8f..10f,
        historicalSuccessRate = 0.84f
    );
}
