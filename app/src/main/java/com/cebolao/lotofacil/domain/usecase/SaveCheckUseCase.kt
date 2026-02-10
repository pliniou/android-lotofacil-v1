package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.CheckHistory
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.repository.CheckHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import javax.inject.Inject

/**
 * UseCase to save a check result to history.
 * Converts CheckResult into CheckHistory and persists it.
 */
class SaveCheckUseCase @Inject constructor(
    private val checkHistoryRepository: CheckHistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) {

    /**
     * Saves a check result with game numbers to history.
     *
     * @param gameNumbers The game numbers that were checked
     * @param contestNumber The contest to use as reference
     * @param checkResult The result of the check
     * @param notes Optional notes for this check
     * @return Flow emitting the saved ID
     */
    operator fun invoke(
        gameNumbers: Set<Int>,
        contestNumber: Int,
        checkResult: CheckResult,
        notes: String? = null
    ): Flow<Long> = flow {
        val checkHistory = CheckHistory(
            gameNumbers = gameNumbers,
            contestNumber = contestNumber,
            checkedAt = Instant.now().toString(),
            hits = checkResult.lastHitScore ?: 0,
            scoreCounts = checkResult.scoreCounts,
            lastHitContest = checkResult.lastHitContest,
            lastHitScore = checkResult.lastHitScore,
            notes = notes
        )

        val savedId = checkHistoryRepository.saveCheck(checkHistory)
        emit(savedId)
    }.flowOn(dispatchersProvider.io)
}
