package com.cebolao.lotofacil.data

import com.cebolao.lotofacil.core.testing.FakeUserPreferencesRepository
import com.cebolao.lotofacil.domain.model.ThemeMode
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

    @Test
    fun `saveThemeMode should update emitted theme mode`() = runTest {
        val repository = FakeUserPreferencesRepository()

        repository.saveThemeMode(ThemeMode.DARK)

        val mode = repository.themeMode.first()
        assertEquals(ThemeMode.DARK, mode)
    }
}
