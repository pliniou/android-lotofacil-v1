package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.CheckHistory
import com.cebolao.lotofacil.domain.repository.CheckHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * UseCase to retrieve check history.
 * Provides various query options for accessing historical checks.
 */
class GetCheckHistoryUseCase @Inject constructor(
    private val checkHistoryRepository: CheckHistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) {

    /**
     * Get all check history records.
     *
     * @return Flow of all check history records
     */
    fun getAll(): Flow<List<CheckHistory>> {
        return checkHistoryRepository.getAllChecks()
            .flowOn(dispatchersProvider.io)
    }

    /**
     * Get checks for a specific contest.
     *
     * @param contestNumber The contest number to filter by
     * @return Flow of check history records for the contest
     */
    fun getByContest(contestNumber: Int): Flow<List<CheckHistory>> {
        return checkHistoryRepository.getChecksByContest(contestNumber)
            .flowOn(dispatchersProvider.io)
    }

    /**
     * Get recent check history.
     *
     * @param limit Maximum number of records (default 50)
     * @return Flow of recent check history records
     */
    fun getRecent(limit: Int = 50): Flow<List<CheckHistory>> {
        return checkHistoryRepository.getRecentChecks(limit)
            .flowOn(dispatchersProvider.io)
    }
}
