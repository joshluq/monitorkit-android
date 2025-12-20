package es.joshluq.monitorkit.domain.model

/**
 * Sealed class representing different types of performance metrics.
 *
 * @property timestamp The time when the metric was captured.
 */
sealed class PerformanceMetric(
    val timestamp: Long = System.currentTimeMillis()
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
        val unit: String
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
        val responseTime: Long
    ) : PerformanceMetric()

    /**
     * Metric for screen loading performance.
     * @property screenName The name of the screen/activity.
     * @property loadTime Time taken to load in milliseconds.
     */
    data class ScreenLoad(
        val screenName: String,
        val loadTime: Long
    ) : PerformanceMetric()
}

/**
 * Supported system resource types.
 */
enum class ResourceType {
    CPU,
    MEMORY
}
