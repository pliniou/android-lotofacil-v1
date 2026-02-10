package com.cebolao.lotofacil.core.utils

import android.util.Log
import com.cebolao.lotofacil.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

interface AppLogger {
    val isDebug: Boolean

    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable? = null, logInRelease: Boolean = false)
    fun e(tag: String, message: String, throwable: Throwable? = null, logInRelease: Boolean = false)
}

@Singleton
class AndroidAppLogger @Inject constructor() : AppLogger {
    override val isDebug: Boolean = BuildConfig.DEBUG

    override fun d(tag: String, message: String) {
        if (isDebug) Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        if (isDebug) Log.i(tag, message)
    }

    override fun w(tag: String, message: String, throwable: Throwable?, logInRelease: Boolean) {
        if (isDebug || logInRelease) {
            if (throwable != null) {
                Log.w(tag, message, throwable)
            } else {
                Log.w(tag, message)
            }
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable?, logInRelease: Boolean) {
        if (isDebug || logInRelease) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }
}
