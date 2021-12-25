package io.cryptem.app.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.cryptem.app.BuildConfig

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */
object L {

    fun d(text: String) {
        val logStrings = createLogStrings(text)
        if (BuildConfig.DEBUG) {
            Log.d(logStrings[0], logStrings[1])
        }
    }

    fun i(text: String) {
        val logStrings = createLogStrings(text)
        if (BuildConfig.DEBUG) {
            Log.i(logStrings[0], logStrings[1])
        }
        FirebaseCrashlytics.getInstance().log("I: $text")
    }

    fun w(text: String) {
        val logStrings = createLogStrings(text)
        if (BuildConfig.DEBUG) {
            Log.w(logStrings[0], logStrings[1])
        }
        FirebaseCrashlytics.getInstance().log("W: $text")
    }

    fun e(text: String) {
        val logStrings = createLogStrings(text)
        if (BuildConfig.DEBUG) {
            Log.e(logStrings[0], logStrings[1])
        }
        FirebaseCrashlytics.getInstance().log("E: $text")
    }

    fun e(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("L", throwable.message, throwable)
            throwable.printStackTrace()
        }
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    private fun createLogStrings(text: String): Array<String> {
        val ste = Thread.currentThread().stackTrace
        val line = "(" + (ste[4].fileName + ":" + ste[4].lineNumber + ")")
        return arrayOf(line, text)
    }
}
