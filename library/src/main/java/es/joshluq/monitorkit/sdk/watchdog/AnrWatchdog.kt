package es.joshluq.monitorkit.sdk.watchdog

import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Watchdog thread that monitors the main thread responsiveness.
 *
 * If the main thread takes longer than [timeoutMs] to process posted work,
 * an ANR is declared and reported via [onAnrDetected].
 *
 * @param timeoutMs Threshold in milliseconds to declare an ANR (default: 5000ms).
 * @param checkIntervalMs Sleep interval to check if the main thread has recovered.
 * @param postToMain Function to schedule execution on the main thread.
 * @param getMainStackTrace Function providing the stack trace of the main thread.
 * @param onAnrDetected Callback invoked when an ANR is detected with duration and formatted stack trace.
 */
internal class AnrWatchdog(
    private val timeoutMs: Long = DEFAULT_TIMEOUT_MS,
    private val checkIntervalMs: Long = DEFAULT_CHECK_INTERVAL_MS,
    private val postToMain: (Runnable) -> Unit = { runnable -> Handler(Looper.getMainLooper()).post(runnable) },
    private val getMainStackTrace: () -> Array<StackTraceElement> = { Looper.getMainLooper().thread.stackTrace },
    private val onAnrDetected: (durationMs: Long, stackTrace: String) -> Unit,
) : Thread("Monitorkit-ANR-Watchdog") {
    companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val DEFAULT_CHECK_INTERVAL_MS = 500L
    }

    private val isRunning = AtomicBoolean(false)

    @Volatile
    private var tick = 0L

    init {
        isDaemon = true
    }

    private val tickerRunnable =
        Runnable {
            if (isRunning.get()) {
                tick = (tick + 1) % Long.MAX_VALUE
            }
        }

    override fun run() {
        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
        } catch (_: Throwable) {
            priority = MIN_PRIORITY
        }

        isRunning.set(true)
        while (isRunning.get() && !isInterrupted) {
            val currentTick = tick
            postToMain(tickerRunnable)

            try {
                sleep(timeoutMs)
            } catch (_: InterruptedException) {
                break
            }

            if (tick == currentTick && isRunning.get()) {
                val stackTraceElements = getMainStackTrace()
                val sb = StringBuilder(stackTraceElements.size * 64)
                for (element in stackTraceElements) {
                    sb.append("    at ").append(element.toString()).append('\n')
                }
                onAnrDetected(timeoutMs, sb.toString())

                while (tick == currentTick && isRunning.get() && !isInterrupted) {
                    try {
                        sleep(checkIntervalMs)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }
        }
    }

    /**
     * Stops the watchdog thread cleanly.
     */
    fun stopWatchdog() {
        isRunning.set(false)
        interrupt()
    }
}
