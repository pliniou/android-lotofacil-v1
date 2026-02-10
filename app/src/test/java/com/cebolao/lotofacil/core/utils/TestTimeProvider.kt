package com.cebolao.lotofacil.core.utils

class TestTimeProvider(
    private var now: Long = 0L
) : TimeProvider {
    override fun currentTimeMillis(): Long = now

    fun advanceBy(deltaMs: Long) {
        now += deltaMs
    }

    fun setCurrentTimeMillis(timeMs: Long) {
        now = timeMs
    }
}
