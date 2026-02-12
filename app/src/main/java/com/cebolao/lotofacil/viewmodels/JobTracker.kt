package com.cebolao.lotofacil.viewmodels

import kotlinx.coroutines.Job

/**
 * Centralized coroutine Job tracker to ensure safe cleanup in onCleared().
 * Thread-safe implementation for concurrent job operations.
 *
 * Usage:
 * ```
 * class MyViewModel : ViewModel() {
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
    private val lock = Any()

    /**
     * Tracks a new Job for later cancellation.
     * @param job The Job to track
     * @return The same Job for chaining
     */
    fun track(job: Job): Job = synchronized(lock) {
        jobs.add(job)
        job
    }

    /**
     * Non-blocking version of track for simple cases.
     * Note: Use suspend version when possible for thread safety.
     */
    fun trackNonBlocking(job: Job): Job {
        return synchronized(lock) {
            jobs.add(job)
            job
        }
    }

    /**
     * Cancels all tracked jobs safely.
     * Thread-safe and removes completed/cancelled jobs from tracking.
     */
    fun cancelAll() = synchronized(lock) {
        jobs.forEach { job ->
            if (!job.isCancelled) {
                job.cancel()
            }
        }
        jobs.clear()
    }
}
