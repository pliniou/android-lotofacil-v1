package com.cebolao.lotofacil.domain.usecase

import javax.inject.Inject

/**
 * UseCase to generate shareable content for games.
 * Prepares game data for sharing via intent or deep linking.
 */
class ShareGameUseCase @Inject constructor() {

    /**
     * Generate a deep link URL for a game.
     *
     * @param gameId Unique identifier for the game
     * @param gameNumbers The game numbers to share
     * @return Deep link URL string (cebolao://game/{id}?numbers=...)
     */
    fun generateDeepLink(gameId: Long, gameNumbers: Set<Int>): String {
        val numbersParam = gameNumbers.sorted().joinToString(",")
        return "cebolao://game/$gameId?numbers=$numbersParam"
    }

    /**
     * Generate shareable text for a game.
     *
     * @param gameNumbers The game numbers
     * @param notes Optional notes about the game
     * @return Formatted text for sharing
     */
    fun generateShareText(gameNumbers: Set<Int>, notes: String? = null): String {
        val numbers = gameNumbers.sorted().joinToString(" ")
        val baseText = "🎰 Meu jogo Lotofácil:\n$numbers"
        return if (notes != null) "$baseText\n\n💬 $notes" else baseText
    }

    /**
     * Generate a game ID from game numbers (deterministic hash).
     * Can be used to identify games for deep linking.
     *
     * @param gameNumbers The game numbers
     * @return A Long ID derived from the numbers
     */
    fun generateGameId(gameNumbers: Set<Int>): Long {
        return gameNumbers.sorted().toString().hashCode().toLong().and(Long.MAX_VALUE)
    }
}
