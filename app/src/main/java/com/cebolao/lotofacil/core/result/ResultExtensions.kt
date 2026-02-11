package com.cebolao.lotofacil.core.result

import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.model.DomainError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for mapping domain errors to user-friendly messages.
 */
@Singleton
class ErrorMessageMapper @Inject constructor(
    private val logger: AppLogger
) {
    
    fun mapError(throwable: Throwable): String {
        logger.e("ErrorMessageMapper", "Error mapped", throwable)
        
        return when (throwable) {
            is DomainError.NetworkError -> "Erro de conexão. Verifique sua internet e tente novamente."
            is DomainError.DataError -> "Erro ao carregar dados. Verifique sua conexão e tente novamente."
            is DomainError.ValidationError -> throwable.message
            is DomainError.NotFoundError -> "${throwable.resource} não encontrado(a)."
            is DomainError.PermissionError -> "Permissão necessária: ${throwable.permission}. Verifique as configurações do app."
            is DomainError.HistoryUnavailable -> "Histórico indisponível no momento. Tente novamente mais tarde."
            is DomainError.InvalidOperation -> "Operação inválida: ${throwable.reason}"
            is DomainError.Unknown -> "Ocorreu um erro inesperado. Tente novamente."
            is DomainError.UnknownError -> "Ocorreu um erro inesperado. Tente novamente."
            else -> "Ocorreu um erro. Tente novamente."
        }
    }
}

/**
 * Extension functions for Flow to work with Results.
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = 
    this.map<T, Result<T>> { Result.success(it) }
        .onStart { emit(Result.loading()) }
        .catch { emit(Result.error(it)) }

/**
 * Creates a Flow that emits Result states for a suspend operation.
 */
fun <T> resultFlow(block: suspend () -> T): Flow<Result<T>> = flow {
    emit(Result.loading())
    try {
        val result = block()
        emit(Result.success(result))
    } catch (e: Exception) {
        emit(Result.error(e))
    }
}

/**
 * Maps domain errors to user-friendly messages in Results.
 */
fun <T> Result<T>.mapErrorMessages(mapper: ErrorMessageMapper): Result<T> {
    return when (this) {
        is Result.Error -> Result.Error(exception, mapper.mapError(exception))
        else -> this
    }
}
