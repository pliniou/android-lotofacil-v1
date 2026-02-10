package com.cebolao.lotofacil.domain.repository

import com.cebolao.lotofacil.domain.model.CheckHistory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for check history management.
 * Defines contracts for persisting and retrieving game check records.
 */
interface CheckHistoryRepository {

    /**
     * Save a check record to the repository.
     *
     * @param checkHistory The check history record to save
     * @return The ID of the saved record
     */
    suspend fun saveCheck(checkHistory: CheckHistory): Long

    /**
     * Delete a specific check record.
     *
     * @param checkHistory The check history record to delete
     */
    suspend fun deleteCheck(checkHistory: CheckHistory)

    /**
     * Get all check history records, ordered by most recent first.
     *
     * @return Flow of check history records
     */
    fun getAllChecks(): Flow<List<CheckHistory>>

    /**
     * Get checks for a specific contest.
     *
     * @param contestNumber The contest number to filter by
     * @return Flow of check history records for the contest
     */
    fun getChecksByContest(contestNumber: Int): Flow<List<CheckHistory>>

    /**
     * Get recent check history records.
     *
     * @param limit Maximum number of records to return (default 50)
     * @return Flow of recent check history records
     */
    fun getRecentChecks(limit: Int = 50): Flow<List<CheckHistory>>

    /**
     * Delete check history records older than a specific date.
     *
     * @param beforeIso8601 ISO 8601 formatted date string
     */
    suspend fun deleteOlderThan(beforeIso8601: String)

    /**
     * Clear all check history.
     */
    suspend fun deleteAll()

    /**
     * Get total count of check history records.
     *
     * @return Total count
     */
    suspend fun getCount(): Int
}
