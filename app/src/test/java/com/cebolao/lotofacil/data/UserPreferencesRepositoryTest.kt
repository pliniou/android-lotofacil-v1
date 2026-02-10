package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.testing.FakeUserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UserPreferencesRepositoryTest {

    @Test
    fun `pinnedGames should emit empty set by default`() = runTest {
        val repository = FakeUserPreferencesRepository()

        val pinned = repository.pinnedGames.first()

        assertEquals(emptySet<String>(), pinned)
    }

    @Test
    fun `savePinnedGames should update emitted set`() = runTest {
        val repository = FakeUserPreferencesRepository()
        val expected = setOf("game-a", "game-b")

        repository.savePinnedGames(expected)

        val pinned = repository.pinnedGames.first()
        assertEquals(expected, pinned)
    }
}
