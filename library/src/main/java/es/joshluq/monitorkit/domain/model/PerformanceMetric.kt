package es.joshluq.monitorkit.domain.model

/**
 * Represents a system performance metric.
 *
 * @property type The type of metric (e.g., CPU, Memory).
 * @property value The measured value of the metric.
 * @property unit The unit of measurement (e.g., %, MB).
 * @property timestamp The time when the metric was captured.
 */
data class PerformanceMetric(
    val type: MetricType,
    val value: Double,
    val unit: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Supported performance metric types.
 */
enum class MetricType {
    CPU,
    MEMORY
}
