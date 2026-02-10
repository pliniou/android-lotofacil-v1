package com.cebolao.lotofacil.data.datasource

import android.content.Context
import com.cebolao.lotofacil.core.coroutine.TestDispatchersProvider
import com.cebolao.lotofacil.core.utils.TestAppLogger
import com.cebolao.lotofacil.data.datasource.database.HistoryDao
import com.cebolao.lotofacil.data.datasource.database.entity.HistoricalDrawEntity
import com.cebolao.lotofacil.data.parser.HistoryParser
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HistoryLocalDataSourceTest {

    private lateinit var dao: HistoryDao
    private lateinit var parser: HistoryParser
    private lateinit var context: Context
    private lateinit var dataSource: HistoryLocalDataSource

    @Before
    fun setup() {
        dao = mock()
        parser = mock()
        context = mock()
        dataSource = HistoryLocalDataSourceImpl(
            context = context,
            historyDao = dao,
            dispatchersProvider = TestDispatchersProvider(),
            parser = parser,
            logger = TestAppLogger()
        )
    }

    @Test
    fun `getHistory should map DAO entities to domain draws`() = runTest {
        val entities = listOf(
            HistoricalDrawEntity(
                contestNumber = 100,
                numbers = (1..15).toSet(),
                date = "01/01/2026"
            )
        )
        whenever(dao.getAll()).thenReturn(flowOf(entities))

        val result = dataSource.getHistory().first()

        assertEquals(1, result.size)
        assertEquals(100, result.first().contestNumber)
        assertEquals((1..15).toSet(), result.first().numbers)
    }

    @Test
    fun `getLatestDraw should return mapped domain draw when available`() = runTest {
        whenever(dao.getLatestDraw()).thenReturn(
            HistoricalDrawEntity(
                contestNumber = 3210,
                numbers = (1..15).toSet(),
                date = "02/01/2026"
            )
        )

        val result = dataSource.getLatestDraw()

        assertEquals(3210, result?.contestNumber)
        assertEquals((1..15).toSet(), result?.numbers)
    }

    @Test
    fun `saveNewContests should return early for empty input`() = runTest {
        val draws = emptyList<HistoricalDraw>()

        dataSource.saveNewContests(draws)

        verify(dao, never()).upsertAll(any())
    }
}
