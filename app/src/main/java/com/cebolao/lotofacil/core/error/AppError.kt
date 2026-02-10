package com.cebolao.lotofacil.core.error

interface AppError {
    val cause: Throwable?
}

object EmptyHistoryError : AppError {
    override val cause: Throwable? = null
}

data class NetworkError(override val cause: Throwable?) : AppError

data class ParsingError(override val cause: Throwable?) : AppError

data class PersistenceError(override val cause: Throwable?) : AppError

data class UnknownError(override val cause: Throwable?) : AppError
