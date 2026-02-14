package com.cebolao.lotofacil

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class CebolaoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val firebaseApp = FirebaseApp.initializeApp(this)
        val crashlytics = firebaseApp?.let { FirebaseCrashlytics.getInstance() }
        crashlytics?.setCrashlyticsCollectionEnabled(true)

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            crashlytics?.recordException(throwable)
            runCatching { ErrorActivity.launch(this, throwable) }
                .onFailure { crashlytics?.recordException(it) }

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable)
            } else {
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(2)
            }
        }
    }
}
