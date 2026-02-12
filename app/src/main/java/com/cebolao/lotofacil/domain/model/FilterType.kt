package com.cebolao.lotofacil.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FilterType(
    val fullRange: ClosedFloatingPointRange<Float>,
    val defaultRange: ClosedFloatingPointRange<Float>
) {
    SOMA_DEZENAS(
        fullRange = 120f..270f,
        defaultRange = 170f..220f
    ),
    PARES(
        fullRange = 0f..12f,
        defaultRange = 6f..9f
    ),
    PRIMOS(
        fullRange = 0f..9f,
        defaultRange = 4f..7f
    ),
    MOLDURA(
        fullRange = 0f..15f,
        defaultRange = 8f..11f
    ),
    RETRATO(
        fullRange = 0f..9f,
        defaultRange = 4f..7f
    ),
    FIBONACCI(
        fullRange = 0f..7f,
        defaultRange = 3f..5f
    ),
    MULTIPLOS_DE_3(
        fullRange = 0f..8f,
        defaultRange = 3f..6f
    ),
    REPETIDAS_CONCURSO_ANTERIOR(
        fullRange = 0f..15f,
        defaultRange = 8f..10f
    );
}
