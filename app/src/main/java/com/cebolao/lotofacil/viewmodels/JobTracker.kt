package com.cebolao.lotofacil.viewmodels

import kotlinx.coroutines.Job

/**
 * Rastreador centralizado de coroutine Jobs para garantir limpeza segura em onCleared().
 *
 * Uso:
 * ```
 * class MyViewModel : BaseViewModel() {
 *     private val jobTracker = JobTracker()
 *
 *     init {
 *         jobTracker.track(viewModelScope.launch { ... })
 *     }
 *
 *     override fun onCleared() {
 *         jobTracker.cancelAll()
 *         super.onCleared()
 *     }
 * }
 * ```
 */
class JobTracker {
    private val jobs = mutableListOf<Job>()

    /**
     * Rastreia um novo Job para cancelamento posterior.
     */
    fun track(job: Job): Job {
        jobs.add(job)
        return job
    }

    /**
     * Cancela todos os Jobs rastreados.
     */
    fun cancelAll() {
        jobs.forEach { job ->
            if (!job.isCancelled) {
                job.cancel()
            }
        }
        jobs.clear()
    }

    /**
     * Retorna True se há Jobs ativos (não completados/cancelados).
     */
    fun hasActiveJobs(): Boolean = jobs.any { !it.isCompleted }
}
