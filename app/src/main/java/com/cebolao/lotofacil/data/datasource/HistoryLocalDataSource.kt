package com.cebolao.lotofacil.data.datasource

import kotlinx.coroutines.flow.map
import android.content.Context
import com.cebolao.lotofacil.core.constants.AppConstants
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.core.extensions.toDomain
import com.cebolao.lotofacil.core.extensions.toEntity
import com.cebolao.lotofacil.data.datasource.database.HistoryDao
import com.cebolao.lotofacil.data.parser.HistoryParser
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.flow.Flow

interface HistoryLocalDataSource {
    fun getHistory(): Flow<List<HistoricalDraw>>
    suspend fun getLatestDraw(): HistoricalDraw?
    suspend fun saveNewContests(newDraws: List<HistoricalDraw>)
    suspend fun populateIfNeeded()
}

@Singleton
class HistoryLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val historyDao: HistoryDao,
    private val dispatchersProvider: DispatchersProvider,
    private val parser: HistoryParser,
    private val logger: AppLogger
) : HistoryLocalDataSource {

    private val historyFileName = AppConstants.HISTORY_ASSET_FILE

    override fun getHistory(): Flow<List<HistoricalDraw>> =
        historyDao.getAll()
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getLatestDraw(): HistoricalDraw? =
        historyDao.getLatestDraw()?.toDomain()

    override suspend fun populateIfNeeded() {
        withContext(dispatchersProvider.io) {
            val dbCount = historyDao.getCount()
            val dbLatest = historyDao.getLatestDraw()?.contestNumber ?: 0

            logger.d("HistoryLocalDataSource", "Checking database population. Count: $dbCount, Latest: $dbLatest")

            val assetDraws = parseHistoryFromAssets()
            val assetLatest = assetDraws.firstOrNull()?.contestNumber ?: 0

            logger.d("HistoryLocalDataSource", "Asset parsed. Count: ${assetDraws.size}, Latest: $assetLatest")

            // If DB is empty OR Assets have newer data, populating/updating.
            // Note: assetDraws is sorted descending by contestNumber in parser.
            if (assetLatest > dbLatest) {
                logger.i("HistoryLocalDataSource", "Assets are newer than DB ($assetLatest > $dbLatest). Updating DB...")
                if (assetDraws.isNotEmpty()) {
                    // upsertAll handles both insertion of new records and updating existing ones.
                    historyDao.upsertAll(assetDraws.map { it.toEntity() })
                    logger.d("HistoryLocalDataSource", "Successfully populated/updated database from assets.")
                }
            } else {
                logger.d("HistoryLocalDataSource", "Database is up to date with assets. Skipping load.")
            }
        }
    }

    override suspend fun saveNewContests(newDraws: List<HistoricalDraw>) {
        if (newDraws.isEmpty()) return
        withContext(dispatchersProvider.io) {
            val uniqueOrderedDraws = newDraws
                .distinctBy { it.contestNumber }
                .sortedByDescending { it.contestNumber }

            historyDao.upsertAll(uniqueOrderedDraws.map { it.toEntity() })
            logger.d("HistoryLocalDataSource", "Persisted ${uniqueOrderedDraws.size} new contests locally.")
        }
    }

    private fun parseHistoryFromAssets(): List<HistoricalDraw> {
        return try {
            context.assets.open(historyFileName).bufferedReader().use { reader ->
                parser.parse(reader.lineSequence())
            }
        } catch (e: IOException) {
            logger.e("HistoryLocalDataSource", "Failed to read history file", e)
            emptyList()
        } catch (e: Exception) {
            logger.e("HistoryLocalDataSource", "Failed to parse history file", e)
            emptyList()
        }
    }
}
