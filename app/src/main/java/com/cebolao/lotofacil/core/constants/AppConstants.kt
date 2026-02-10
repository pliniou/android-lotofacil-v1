package com.cebolao.lotofacil.core.constants

/**
 * Application-wide constants for magic numbers and configuration values.
 */
object AppConstants {
    // Game generation
    const val GAME_SIZE = 15
    const val MAX_GAME_GENERATION_ATTEMPTS = 50_000
    val LOTOFACIL_NUMBER_RANGE = 1..25
 
    // File names
    const val HISTORY_ASSET_FILE = "lotofacil_resultados.txt"
}
