package es.joshluq.monitorkit.domain.model

/**
 * Represents a custom event to be tracked by the library.
 *
 * @property name The unique name identifying the event.
 * @property properties A map of additional data associated with the event.
 * @property timestamp The time when the event occurred.
 */
data class MonitorEvent(
    val name: String,
    val properties: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
