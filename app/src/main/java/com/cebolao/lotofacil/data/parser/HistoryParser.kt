package com.cebolao.lotofacil.data.parser

import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import javax.inject.Inject

/**
 * Parses lines from the history text file stored in the app's assets.  Each
 * line should contain the contest number and the 15 drawn numbers separated
 * by a hyphen (e.g. "1234 – 01,02,03,...").
 */
class HistoryParser @Inject constructor(
    private val logger: AppLogger
) {
    private val lineRegex = "^\\d+\\s*-\\s*[\\d, ]+$".toRegex()

    /**
     * Parse the given raw text into a list of [HistoricalDraw] objects.  Any
     * malformed lines are skipped.  Errors are logged only in debug builds.
     */
    fun parse(lines: Sequence<String>): List<HistoricalDraw> = lines
        .filter { it.isNotBlank() && it.matches(lineRegex) }
        .mapNotNull { parseLine(it) }
        .sortedByDescending { it.contestNumber }
        .toList()

    private fun parseLine(line: String): HistoricalDraw? {
        return try {
            val parts = line.split(" - ", limit = 2)
            if (parts.size != 2) {
                logger.w(TAG, "Invalid line format: $line")
                return null
            }
            val contestNumber = parts[0].trim().toInt()
            val numbers = parts[1].split(',')
                .mapNotNull { it.trim().toIntOrNull() }
                .filter { it in LotofacilConstants.VALID_NUMBER_RANGE }
                .toSet()
            if (contestNumber > 0 && numbers.size == LotofacilConstants.GAME_SIZE) {
                HistoricalDraw(contestNumber, numbers)
            } else {
                logger.w(TAG, "Invalid data: contest=$contestNumber, numbers=$numbers")
                null
            }
        } catch (e: NumberFormatException) {
            logger.w(TAG, "Number parse error: $line", e)
            null
        } catch (e: Exception) {
            logger.e(TAG, "Unexpected error parsing: $line", e)
            null
        }
    }

    companion object {
        private const val TAG = "HistoryParser"
    }
}
