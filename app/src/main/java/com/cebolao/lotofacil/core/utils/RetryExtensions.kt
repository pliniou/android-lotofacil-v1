package com.cebolao.lotofacil.core.utils

import kotlinx.coroutines.delay

/**
 * Executa uma operação com retry exponencial.
 *
 * @param maxRetries Número máximo de tentativas (padrão: 3)
 * @param initialDelayMs Delay inicial em milissegundos (padrão: 100ms)
 * @param multiplier Multiplicador de delay (padrão: 2.0 para exponencial)
 * @param maxDelayMs Delay máximo em milissegundos (padrão: 10s)
 * @param block Operação a executar
 * @return Resultado da operação
 */
suspend fun <T> retryExponentialBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 100,
    multiplier: Double = 2.0,
    maxDelayMs: Long = 10000,
    block: suspend () -> T
): T {
    var currentDelayMs = initialDelayMs
    var lastException: Exception? = null

    for (attempt in 0..maxRetries) {
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxRetries) {
                delay(currentDelayMs)
                currentDelayMs = (currentDelayMs * multiplier).toLong().coerceAtMost(maxDelayMs)
            }
        }
    }

    throw lastException ?: RuntimeException("Max retries exceeded")
}
