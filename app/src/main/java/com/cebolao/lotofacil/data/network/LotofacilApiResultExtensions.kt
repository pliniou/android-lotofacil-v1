package com.cebolao.lotofacil.data.network

import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.model.PrizeTier
import com.cebolao.lotofacil.domain.model.WinnerLocation

fun LotofacilApiResult.toHistoricalDraw(): HistoricalDraw? {
    val contestNumber = concurso ?: numero ?: return null
    val numbers = (dezenas ?: listaDezenas)
        ?.mapNotNull { it.toIntOrNull() }
        ?.filter { it in LotofacilConstants.VALID_NUMBER_RANGE }
        ?.toSet()
    if (numbers == null || numbers.size < LotofacilConstants.GAME_SIZE) {
        return null
    }

    return HistoricalDraw(
        contestNumber = contestNumber,
        numbers = numbers,
        date = dataApuracao ?: data,
        prizes = (premiacoes ?: listaRateioPremio).orEmpty().mapNotNull { tier ->
            val description = tier.descricao ?: tier.descricaoFaixa
            val winners = tier.ganhadores ?: tier.numeroDeGanhadores
            val prizeValue = tier.valorPremio
            if (description.isNullOrBlank() || winners == null || prizeValue == null) {
                return@mapNotNull null
            }
            PrizeTier(
                faixa = tier.faixa,
                description = description,
                winners = winners,
                prizeValue = prizeValue
            )
        },
        winners = (localGanhadores ?: listaMunicipioUFGanhadores).orEmpty().mapNotNull { location ->
            val winnersCount = location.ganhadores ?: return@mapNotNull null
            WinnerLocation(
                winnersCount = winnersCount,
                city = location.municipio.orEmpty(),
                state = location.uf.orEmpty()
            )
        },
        nextContest = proximoConcurso ?: numeroConcursoProximo ?: numeroProximoConcurso,
        nextDate = dataProximoConcurso,
        nextEstimate = valorEstimadoProximoConcurso,
        accumulated = acumulou ?: acumulado ?: false
    )
}
