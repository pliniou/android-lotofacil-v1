package com.cebolao.lotofacil.data.network

import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.model.PrizeTier
import com.cebolao.lotofacil.domain.model.WinnerLocation

fun LotofacilApiResult.toHistoricalDraw(): HistoricalDraw? {
    val contestNumber = concurso ?: return null
    val numbers = dezenas
        ?.mapNotNull { it.toIntOrNull() }
        ?.filter { it in LotofacilConstants.VALID_NUMBER_RANGE }
        ?.toSet()
    if (numbers == null || numbers.size < LotofacilConstants.GAME_SIZE) {
        return null
    }

    return HistoricalDraw(
        contestNumber = contestNumber,
        numbers = numbers,
        date = dataApuracao,
        prizes = premiacoes?.map {
            PrizeTier(
                description = it.descricao,
                winners = it.ganhadores,
                prizeValue = it.valorPremio
            )
        } ?: emptyList(),
        winners = localGanhadores?.map {
            WinnerLocation(
                winnersCount = it.ganhadores,
                city = it.municipio.orEmpty(),
                state = it.uf.orEmpty()
            )
        } ?: emptyList(),
        nextContest = proximoConcurso,
        nextDate = dataProximoConcurso,
        nextEstimate = valorEstimadoProximoConcurso,
        accumulated = acumulou ?: false
    )
}
