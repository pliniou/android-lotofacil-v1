package com.cebolao.lotofacil.data.network

import kotlinx.serialization.Serializable

@Serializable
data class LotofacilApiResult(
    val concurso: Int? = null,
    val dezenas: List<String>? = null,
    val dataApuracao: String? = null,
    val premiacoes: List<Premiacao>? = null,
    val localGanhadores: List<LocalGanhador>? = null,
    val proximoConcurso: Int? = null,
    val dataProximoConcurso: String? = null,
    val valorEstimadoProximoConcurso: Double? = null,
    val acumulou: Boolean? = null
)

@Serializable
data class Premiacao(
    val descricao: String,
    val faixa: Int,
    val ganhadores: Int,
    val valorPremio: Double
)

@Serializable
data class LocalGanhador(
    val ganhadores: Int,
    val municipio: String? = null,
    val uf: String? = null
)
