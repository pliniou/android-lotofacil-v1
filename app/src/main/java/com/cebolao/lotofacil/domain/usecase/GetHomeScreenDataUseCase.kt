package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.toSuccess
import com.cebolao.lotofacil.domain.model.HomeLastContest
import com.cebolao.lotofacil.domain.model.HomeNextContest
import com.cebolao.lotofacil.domain.model.HomeNextContestSource
import com.cebolao.lotofacil.domain.model.HomePrizeTier
import com.cebolao.lotofacil.domain.model.HomeWinnerLocation
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class HomeScreenData(
    val lastContest: HomeLastContest?,
    val nextContest: HomeNextContest?,
    val initialStats: StatisticsReport,
    val history: List<HistoricalDraw>,
    val statisticsSource: StatisticsDataSource,
    val isShowingStaleStatistics: Boolean
)

class GetHomeScreenDataUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val getStatisticsDataUseCase: GetStatisticsDataUseCase,
    private val dispatchersProvider: DispatchersProvider
) {
    operator fun invoke(): Flow<AppResult<HomeScreenData>> {
        return historyRepository.getHistory()
            .map { history ->
                if (history.isEmpty()) {
                    AppResult.Failure(EmptyHistoryError)
                } else {
                    val lastDraw = history.first()
                    val lastContest = lastDraw.toHomeLastContest()
                    val nextContest = lastDraw.toHomeNextContest()
                    when (
                        val reportResult = getStatisticsDataUseCase.loadReportForHistory(
                            history = history,
                            timeWindow = 0,
                            forceRefresh = false
                        )
                    ) {
                        is AppResult.Success -> {
                            HomeScreenData(
                                lastContest = lastContest,
                                nextContest = nextContest,
                                initialStats = reportResult.value.report,
                                history = history,
                                statisticsSource = reportResult.value.source,
                                isShowingStaleStatistics = reportResult.value.isStale
                            ).toSuccess()
                        }

                        is AppResult.Failure -> reportResult
                    }
                }
            }
            .distinctUntilChanged { old, new ->
                // Optimize: Only re-emit/re-calculate if history size or latest contest changed
                if (old is AppResult.Success && new is AppResult.Success) {
                    val oldData = old.value
                    val newData = new.value
                    oldData.history.size == newData.history.size &&
                            oldData.history.firstOrNull()?.contestNumber == newData.history.firstOrNull()?.contestNumber
                } else {
                    old == new
                }
            }
            .flowOn(dispatchersProvider.io)
    }
}

private val homeDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun HistoricalDraw.toHomeLastContest(): HomeLastContest {
    return HomeLastContest(
        contest = contestNumber,
        date = date,
        numbers = numbers.toImmutableSet(),
        sum = sum,
        evens = evens,
        odds = odds,
        primes = primes,
        frame = frame,
        portrait = portrait,
        fibonacci = fibonacci,
        multiplesOf3 = multiplesOf3,
        prizes = prizes.map { tier ->
            HomePrizeTier(
                faixa = tier.faixa,
                description = tier.description,
                winners = tier.winners,
                prizeValue = tier.prizeValue
            )
        },
        winnerLocations = winners.map { location ->
            HomeWinnerLocation(
                winnersCount = location.winnersCount,
                city = location.city.takeIf { it.isNotBlank() },
                state = location.state.takeIf { it.isNotBlank() }
            )
        },
        accumulated = accumulated
    )
}

fun HistoricalDraw.toHomeNextContest(): HomeNextContest {
    val hasOfficialNextData = nextContest != null || !nextDate.isNullOrBlank() || nextEstimate != null
    val derivedContest = contestNumber + 1
    val derivedDate = deriveNextDate(date)
    val contest = nextContest ?: derivedContest
    val date = nextDate ?: derivedDate
    val isDerivedPrediction = !hasOfficialNextData || nextContest == null || nextDate.isNullOrBlank()
    val source = if (isDerivedPrediction) HomeNextContestSource.DERIVED else HomeNextContestSource.OFFICIAL

    return HomeNextContest(
        contestNumber = contest,
        date = date,
        prizeEstimate = nextEstimate,
        isAccumulated = accumulated,
        source = source
    )
}

private fun deriveNextDate(lastContestDate: String?): String? {
    if (lastContestDate.isNullOrBlank()) return null
    val parsedDate = try {
        LocalDate.parse(lastContestDate, homeDateFormatter)
    } catch (_: DateTimeParseException) {
        return null
    }

    var nextDate = parsedDate.plusDays(1)
    if (nextDate.dayOfWeek == DayOfWeek.SUNDAY) {
        nextDate = nextDate.plusDays(1)
    }
    return nextDate.format(homeDateFormatter)
}
