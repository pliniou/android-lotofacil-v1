package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.PrizeTier
import com.cebolao.lotofacil.domain.model.WinnerLocation
import kotlinx.serialization.json.Json

@Entity(tableName = "historical_draws")
data class HistoricalDrawEntity(
    @PrimaryKey
    val contestNumber: Int,
    val numbers: Set<Int>,
    val date: String? = null,
    val prizes: List<PrizeTier> = emptyList(),
    val winners: List<WinnerLocation> = emptyList(),
    val nextContest: Int? = null,
    val nextDate: String? = null,
    val nextEstimate: Double? = null,
    val accumulated: Boolean = false
)



