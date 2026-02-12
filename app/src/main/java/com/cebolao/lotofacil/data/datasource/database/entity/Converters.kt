package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.TypeConverter
import com.cebolao.lotofacil.domain.model.PrizeTier
import com.cebolao.lotofacil.domain.model.WinnerLocation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromIntSet(value: Set<Int>?): String {
        return value?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toIntSet(value: String?): Set<Int> {
        return value?.let {
            try {
                json.decodeFromString(it)
            } catch (e: Exception) {
                emptySet()
            }
        } ?: emptySet()
    }

    @TypeConverter
    fun fromPrizeTierList(value: List<PrizeTier>?): String {
        return value?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toPrizeTierList(value: String?): List<PrizeTier> {
        return value?.let {
            try {
                json.decodeFromString(it)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    @TypeConverter
    fun fromWinnerLocationList(value: List<WinnerLocation>?): String {
        return value?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toWinnerLocationList(value: String?): List<WinnerLocation> {
        return value?.let {
            try {
                json.decodeFromString(it)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String {
        return value?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int> {
        return value?.let {
            try {
                json.decodeFromString(it)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }
}
