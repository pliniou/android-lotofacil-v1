package com.cebolao.lotofacil.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("latest")
    suspend fun getLatestResult(): LotofacilApiResult

    @GET("{contest}")
    suspend fun getResultByContest(@Path("contest") contestNumber: Int): LotofacilApiResult
}
