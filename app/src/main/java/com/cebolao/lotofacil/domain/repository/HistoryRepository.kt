package com.cebolao.lotofacil.domain.repository

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Define o status da sincronização de dados de rede.
 * Permite que a UI reaja a diferentes estados do processo de atualização.
 */
sealed interface SyncStatus {
    object Idle : SyncStatus
    object Syncing : SyncStatus
    object Success : SyncStatus
    data class Failed(val message: String) : SyncStatus
}

interface HistoryRepository {
    /**
     * Um StateFlow que emite o status atual da sincronização de dados de rede.
     * Substitui o booleano `isSyncing` para fornecer informações mais detalhadas.
     */
    val syncStatus: StateFlow<SyncStatus>
    val isInitialized: StateFlow<Boolean>

    fun getHistory(): Flow<List<HistoricalDraw>>
    suspend fun getLastDraw(): HistoricalDraw?
    suspend fun syncHistory(): AppResult<Unit>
}
