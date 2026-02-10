package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.error.AppError
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.GameStatistic

enum class GameCheckPhase {
    HISTORICAL,
    CALCULATION,
    STATISTICS
}

sealed interface GameCheckState {
    data class InProgress(val phase: GameCheckPhase, val progress: Float) : GameCheckState
    data class Success(val result: CheckResult, val stats: List<GameStatistic>) : GameCheckState
    data class Failure(val error: AppError) : GameCheckState
}
