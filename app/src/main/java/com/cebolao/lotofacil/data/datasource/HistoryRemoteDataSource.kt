package com.cebolao.lotofacil.data.datasource

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.core.utils.retryExponentialBackoff
import com.cebolao.lotofacil.data.network.ApiService
import com.cebolao.lotofacil.data.network.toHistoricalDraw
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

interface HistoryRemoteDataSource {
    suspend fun getLatestDraw(): HistoricalDraw?
    suspend fun getDrawsInRange(range: IntRange): List<HistoricalDraw>
}

@Singleton
class HistoryRemoteDataSourceImpl @Inject constructor(
    @Named("CaixaApi") private val caixaService: ApiService,
    @Named("HerokuApi") private val herokuService: ApiService,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: AppLogger
) : HistoryRemoteDataSource {

    companion object {
        private const val TAG = "HistoryRemoteDataSource"
        private const val BATCH_SIZE = 30
        // Reduced to work with rate limiter (30 requests/60s = ~2s per request on average)
        private const val MAX_CONCURRENT_REQUESTS = 3
        private const val RETRY_ATTEMPTS = 2
        private const val RETRY_DELAY_MS = 250L
        // Small delay between batches to let rate limiter recover
        private const val INTER_BATCH_DELAY_MS = 500L
    }

    // Global semaphore to limit concurrent network requests
    private val networkSemaphore = Semaphore(MAX_CONCURRENT_REQUESTS)

    override suspend fun getLatestDraw(): HistoricalDraw? = withContext(dispatchersProvider.io) {
        // Try Caixa first
        try {
            val result = retry { caixaService.getLatestResult() }
            val draw = result.toHistoricalDraw()
            logger.d(TAG, "Successfully fetched latest draw from Caixa API")
            return@withContext draw
        } catch (e: Exception) {
            logger.w(TAG, "Failed to fetch latest draw from Caixa API, falling back to Heroku", e)
        }

        // Fallback to Heroku
        try {
            val result = retry { herokuService.getLatestResult() }
            val draw = result.toHistoricalDraw()
            logger.d(TAG, "Successfully fetched latest draw from Heroku API")
            return@withContext draw
        } catch (e: Exception) {
            logger.e(TAG, "Failed to fetch latest draw from Heroku (fallback)", e)
            null
        }
    }

    override suspend fun getDrawsInRange(range: IntRange): List<HistoricalDraw> =
        withContext(dispatchersProvider.io) {
        if (range.isEmpty()) return@withContext emptyList()

        val results = mutableListOf<HistoricalDraw>()
        val windows = range.chunked(MAX_CONCURRENT_REQUESTS)
        
        coroutineScope {
            windows.forEachIndexed { index, window ->
                val batchResults = window.map { contestNumber ->
                    async {
                        networkSemaphore.withPermit {
                            fetchContest(contestNumber)
                        }
                    }
                }.awaitAll().filterNotNull()
                
                results.addAll(batchResults)
                
                // Add delay between batches to avoid overwhelming the rate limiter
                if (index < windows.lastIndex) {
                    delay(INTER_BATCH_DELAY_MS)
                }
            }
        }
        
        results
    }

    private suspend fun fetchContest(contestNumber: Int): HistoricalDraw? {
        // Try Caixa first
        try {
            val result = retry { caixaService.getResultByContest(contestNumber) }
            val draw = result.toHistoricalDraw()
            // logger.v(TAG, "Fetched contest $contestNumber from Caixa") // Verbose logging if needed
            return draw
        } catch (e: Exception) {
            logger.w(TAG, "Failed to fetch contest $contestNumber from Caixa, attempting fallback", e)
        }

        // Fallback to Heroku
        try {
            val result = retry { herokuService.getResultByContest(contestNumber) }
            val draw = result.toHistoricalDraw()
            logger.d(TAG, "Fetched contest $contestNumber from Heroku (fallback)")
            return draw
        } catch (e: Exception) {
            logger.w(TAG, "Failed to fetch contest $contestNumber from Heroku (fallback)", e)
            return null
        }
    }

    private suspend fun <T> retry(
        attempts: Int = RETRY_ATTEMPTS,
        block: suspend () -> T
    ): T {
        return retryExponentialBackoff(
            maxRetries = attempts - 1,  // maxRetries não inclui a tentativa inicial
            initialDelayMs = RETRY_DELAY_MS,
            multiplier = 2.0,
            maxDelayMs = 5000L,
            block = block
        )
    }
}
