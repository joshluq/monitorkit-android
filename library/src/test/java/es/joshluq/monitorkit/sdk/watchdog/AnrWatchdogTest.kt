package es.joshluq.monitorkit.sdk.watchdog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class AnrWatchdogTest {
    @Test
    fun `watchdog should not trigger ANR when main thread processes runnable`() {
        val anrTriggered = AtomicBoolean(false)
        val latch = CountDownLatch(3)

        val watchdog =
            AnrWatchdog(
                timeoutMs = 40L,
                checkIntervalMs = 20L,
                postToMain = { runnable ->
                    runnable.run()
                    latch.countDown()
                },
                getMainStackTrace = { emptyArray() },
                onAnrDetected = { _, _ ->
                    anrTriggered.set(true)
                },
            )

        watchdog.start()
        val completed = latch.await(1000, TimeUnit.MILLISECONDS)
        watchdog.stopWatchdog()
        watchdog.join(500)

        assertTrue(completed)
        assertFalse(anrTriggered.get())
    }

    @Test
    fun `watchdog should trigger ANR when main thread does not process runnable`() {
        val detectedDuration = AtomicLong(0L)
        val detectedStackTrace = AtomicReference<String>()
        val anrLatch = CountDownLatch(1)

        val fakeStackTrace =
            arrayOf(
                StackTraceElement("com.example.HeavyTask", "doWork", "HeavyTask.kt", 42),
            )

        val watchdog =
            AnrWatchdog(
                timeoutMs = 50L,
                checkIntervalMs = 20L,
                postToMain = { _ ->
                    // Do nothing: simulate blocked main thread
                },
                getMainStackTrace = { fakeStackTrace },
                onAnrDetected = { durationMs, stackTrace ->
                    detectedDuration.set(durationMs)
                    detectedStackTrace.set(stackTrace)
                    anrLatch.countDown()
                },
            )

        watchdog.start()
        val triggered = anrLatch.await(1000, TimeUnit.MILLISECONDS)
        watchdog.stopWatchdog()
        watchdog.join(500)

        assertTrue(triggered)
        assertEquals(50L, detectedDuration.get())
        assertTrue(detectedStackTrace.get().contains("HeavyTask.kt:42"))
    }

    @Test
    fun `stopWatchdog should interrupt and terminate thread`() {
        val watchdog =
            AnrWatchdog(
                timeoutMs = 1000L,
                checkIntervalMs = 200L,
                postToMain = { _ -> },
                getMainStackTrace = { emptyArray() },
                onAnrDetected = { _, _ -> },
            )

        watchdog.start()
        assertTrue(watchdog.isAlive)

        watchdog.stopWatchdog()
        watchdog.join(1000)

        assertFalse(watchdog.isAlive)
    }
}
