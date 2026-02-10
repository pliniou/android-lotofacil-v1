package com.cebolao.lotofacil.core.utils

class TestAppLogger(
    override val isDebug: Boolean = true
) : AppLogger {
    override fun d(tag: String, message: String) = Unit
    override fun i(tag: String, message: String) = Unit
    override fun w(tag: String, message: String, throwable: Throwable?, logInRelease: Boolean) = Unit
    override fun e(tag: String, message: String, throwable: Throwable?, logInRelease: Boolean) = Unit
}
