package com.cebolao.lotofacil.core.utils

import java.text.NumberFormat
import java.util.Locale

object NumberFormatUtils {
    private val lotteryNumberFormat: NumberFormat by lazy {
        NumberFormat.getInstance(Locale.getDefault()).apply {
            minimumIntegerDigits = 2
            isGroupingUsed = false
        }
    }

    private val integerFormat: NumberFormat by lazy {
        NumberFormat.getIntegerInstance(Locale.getDefault())
    }

    private val currencyFormat: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    }

    /**
     * Formats a lottery number with at least 2 digits (e.g., 01, 15).
     */
    fun formatLotteryNumber(number: Int): String {
        return lotteryNumberFormat.format(number)
    }

    /**
     * Formats an integer using the default locale's rules (e.g., 1.000 in PT-BR).
     */
    fun formatInteger(number: Int): String {
        return integerFormat.format(number)
    }
    
    /**
     * Formats an integer using the default locale's rules (e.g., 1.000 in PT-BR).
     */
    fun formatInteger(number: Long): String {
        return integerFormat.format(number)
    }

    /**
     * Formats a double as currency (BRL).
     */
    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }
}
