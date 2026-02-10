package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.UserStats
import com.cebolao.lotofacil.domain.service.UserStatisticsService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserGameStatisticsUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val historyRepository: HistoryRepository,
    private val userStatisticsService: UserStatisticsService
) {
    suspend operator fun invoke(): AppResult<UserStats> {
        return try {
            val games = gameRepository.games.value
            val history = historyRepository.getHistory().first()
            
            AppResult.Success(
                userStatisticsService.calculateUserStats(games, history)
            )
        } catch (e: Exception) {
            AppResult.Failure(DomainError.Unknown(e))
        }
    }
}
