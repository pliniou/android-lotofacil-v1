package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.PrizeTier
import com.cebolao.lotofacil.domain.model.WinnerLocation
import kotlinx.serialization.encodeToString
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

// Mapping extension functions
fun HistoricalDrawEntity.toDomain() = HistoricalDraw(
    contestNumber = contestNumber,
    numbers = numbers,
    date = date,
    prizes = prizes,
    winners = winners,
    nextContest = nextContest,
    nextDate = nextDate,
    nextEstimate = nextEstimate,
    accumulated = accumulated
)

fun HistoricalDraw.toEntity() = HistoricalDrawEntity(
    contestNumber = contestNumber,
    numbers = numbers,
    date = date,
    prizes = prizes,
    winners = winners,
    nextContest = nextContest,
    nextDate = nextDate,
    nextEstimate = nextEstimate,
    accumulated = accumulated
)

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromIntSet(value: Set<Int>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toIntSet(value: String): Set<Int> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptySet()
        }
    }

    @TypeConverter
    fun fromPrizeList(value: List<PrizeTier>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toPrizeList(value: String): List<PrizeTier> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromWinnerLocationList(value: List<WinnerLocation>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toWinnerLocationList(value: String): List<WinnerLocation> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
