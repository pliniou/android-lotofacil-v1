package com.cebolao.lotofacil

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class CebolaoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { ErrorActivity.launch(this, throwable) }

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable)
            } else {
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(2)
            }
        }
    }
}
