package com.cebolao.lotofacil.data.datasource

import kotlinx.coroutines.flow.map

import android.content.Context
import com.cebolao.lotofacil.core.constants.AppConstants
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.data.datasource.database.HistoryDao
import com.cebolao.lotofacil.data.datasource.database.entity.toDomain
import com.cebolao.lotofacil.data.datasource.database.entity.toEntity
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
            logger.d("HistoryLocalDataSource", "Checking database population. Current count: $dbCount")
            if (dbCount == 0) {
                val assetDraws = parseHistoryFromAssets()
                logger.d("HistoryLocalDataSource", "Parsed ${assetDraws.size} draws from assets.")
                if (assetDraws.isNotEmpty()) {
                    historyDao.upsertAll(assetDraws.map { it.toEntity() })
                    logger.d("HistoryLocalDataSource", "Successfully populated database from assets.")
                } else {
                    logger.w("HistoryLocalDataSource", "Asset file was empty or failed to parse.")
                }
            } else {
                logger.d("HistoryLocalDataSource", "Database already populated. Skipping asset load.")
            }
        }
    }

    override suspend fun saveNewContests(newDraws: List<HistoricalDraw>) {
        if (newDraws.isEmpty()) return
        withContext(dispatchersProvider.io) {
            historyDao.upsertAll(newDraws.map { it.toEntity() })
            logger.d("HistoryLocalDataSource", "Persisted ${newDraws.size} new contests locally.")
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
