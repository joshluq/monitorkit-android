package es.joshluq.monitorkit.sdk.logger

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for logging that gracefully handles Unit Test environments.
 * It attempts to use [android.util.Log], but falls back to [println] if the Android classes are not mocked.
 * This ensures that tests do not fail with "Method not mocked" errors when logging.
 */
@Singleton
class Logger @Inject constructor() {

    /**
     * Logs a debug message.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    fun d(tag: String, message: String) {
        try {
            Log.d(tag, message)
        } catch (e: RuntimeException) {
            // Fallback for Unit Tests where Log.d is not mocked
            println("DEBUG: [$tag] $message")
        }
    }

    /**
     * Logs a warning message.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    fun w(tag: String, message: String) {
        try {
            Log.w(tag, message)
        } catch (e: RuntimeException) {
            // Fallback for Unit Tests where Log.w is not mocked
            println("WARN: [$tag] $message")
        }
    }

    /**
     * Logs an error message.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     * @param throwable An optional exception to log.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        try {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        } catch (e: RuntimeException) {
            // Fallback for Unit Tests where Log.e is not mocked
            println("ERROR: [$tag] $message")
            throwable?.printStackTrace()
        }
    }
}
