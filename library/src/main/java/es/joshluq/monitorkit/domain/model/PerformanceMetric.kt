package es.joshluq.monitorkit.domain.model

/**
 * Sealed class representing different types of performance metrics.
 *
 * @property timestamp The time when the metric was captured.
 */
sealed class PerformanceMetric(
    val timestamp: Long = System.currentTimeMillis(),
) {
    /**
     * Metric for system resource usage.
     * @property type The type of resource (CPU, MEMORY).
     * @property value The measured value.
     * @property unit The unit of measurement (%, MB).
     */
    data class Resource(
        val type: ResourceType,
        val value: Double,
        val unit: String,
    ) : PerformanceMetric()

    /**
     * Metric for network operations.
     * @property url The requested URL.
     * @property method HTTP method used (GET, POST, etc.).
     * @property statusCode HTTP response status code.
     * @property responseTime Time taken in milliseconds.
     */
    data class Network(
        val url: String,
        val method: String,
        val statusCode: Int,
        val responseTime: Long,
    ) : PerformanceMetric()

    /**
     * Metric for screen loading performance.
     * @property screenName The name of the screen/activity.
     * @property loadTime Time taken to load in milliseconds.
     */
    data class ScreenLoad(
        val screenName: String,
        val loadTime: Long,
    ) : PerformanceMetric()

    /**
     * Metric for custom process duration (Trace).
     * @property name The unique name/key of the trace (e.g., "login_process").
     * @property durationMs The total duration of the process in milliseconds.
     * @property properties Additional properties associated with the trace.
     */
    data class Trace(
        val name: String,
        val durationMs: Long,
        val properties: Map<String, Any>? = null,
    ) : PerformanceMetric()

    /**
     * Metric for UI frame rendering latency and slow or frozen frames.
     *
     * @property screenName The screen or activity where jank was detected.
     * @property durationMs The duration taken to render the frame in milliseconds.
     * @property isFrozen Whether the frame exceeded the frozen threshold (typically > 700ms).
     */
    data class Jank(
        val screenName: String,
        val durationMs: Long,
        val isFrozen: Boolean = false,
    ) : PerformanceMetric()

    /**
     * Metric captured when the main thread is blocked (Application Not Responding).
     *
     * @property durationMs The approximate duration in milliseconds the main thread was unresponsive.
     * @property stackTrace The captured stack trace of the main thread at the moment of the blockage.
     */
    data class Anr(
        val durationMs: Long,
        val stackTrace: String,
    ) : PerformanceMetric()

    /**
     * Metric for application launch duration.
     *
     * @property processType The type of launch (e.g. "COLD", "WARM", "HOT").
     * @property durationMs Time taken in milliseconds from start to interactive display.
     */
    data class AppStart(
        val processType: String,
        val durationMs: Long,
    ) : PerformanceMetric()
}

/**
 * Supported system resource types.
 */
enum class ResourceType {
    CPU,
    MEMORY,
}
