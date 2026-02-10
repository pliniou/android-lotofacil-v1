package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.testing.FakeGameRepository
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetSavedGamesUseCaseTest {

    private lateinit var gameRepository: FakeGameRepository
    private lateinit var useCase: GetSavedGamesUseCase

    @Before
    fun setup() {
        gameRepository = FakeGameRepository()
        useCase = GetSavedGamesUseCase(gameRepository)
    }

    @Test
    fun `invoke should return empty list when there are no games`() = runTest {
        val result = useCase().first()
        assertEquals(0, result.size)
    }

    @Test
    fun `invoke should sort pinned first then by creation timestamp desc`() = runTest {
        val oldUnpinned = LotofacilGame(numbers = (1..15).toSet(), creationTimestamp = 100L)
        val newerUnpinned = LotofacilGame(numbers = (2..16).toSet(), creationTimestamp = 200L)
        val toPin = LotofacilGame(numbers = (3..17).toSet(), creationTimestamp = 50L)

        gameRepository.addGeneratedGames(listOf(oldUnpinned, newerUnpinned, toPin))
        gameRepository.togglePinState(toPin)

        val result = useCase().first()

        assertEquals(3, result.size)
        assertEquals(toPin.id, result[0].id)
        assertEquals(newerUnpinned.id, result[1].id)
        assertEquals(oldUnpinned.id, result[2].id)
    }
}
