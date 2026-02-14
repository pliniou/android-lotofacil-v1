package com.cebolao.lotofacil.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LotofacilApiResult(
    val concurso: Int? = null,
    @SerialName("numero") val numero: Int? = null,
    val dezenas: List<String>? = null,
    @SerialName("listaDezenas") val listaDezenas: List<String>? = null,
    val dataApuracao: String? = null,
    @SerialName("data") val data: String? = null,
    val premiacoes: List<Premiacao>? = null,
    @SerialName("listaRateioPremio") val listaRateioPremio: List<Premiacao>? = null,
    val localGanhadores: List<LocalGanhador>? = null,
    @SerialName("listaMunicipioUFGanhadores")
    val listaMunicipioUFGanhadores: List<LocalGanhador>? = null,
    val proximoConcurso: Int? = null,
    @SerialName("numeroConcursoProximo") val numeroConcursoProximo: Int? = null,
    @SerialName("numeroProximoConcurso") val numeroProximoConcurso: Int? = null,
    val dataProximoConcurso: String? = null,
    val valorEstimadoProximoConcurso: Double? = null,
    val acumulou: Boolean? = null,
    @SerialName("acumulado") val acumulado: Boolean? = null
)

@Serializable
data class Premiacao(
    val descricao: String? = null,
    @SerialName("descricaoFaixa") val descricaoFaixa: String? = null,
    val faixa: Int? = null,
    val ganhadores: Int? = null,
    @SerialName("numeroDeGanhadores") val numeroDeGanhadores: Int? = null,
    val valorPremio: Double? = null
)

@Serializable
data class LocalGanhador(
    val ganhadores: Int? = null,
    val municipio: String? = null,
    val uf: String? = null
)
