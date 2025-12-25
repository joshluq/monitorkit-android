package es.joshluq.monitorkit.data.provider

import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric

/**
 * Interface to be implemented by the consumer application to provide
 * integration with third-party monitoring services (e.g., Firebase, Sentry).
 */
interface MonitorProvider {
    /**
     * Unique identifier for the provider.
     */
    val key: String

    /**
     * Sends a custom event to the monitoring service.
     *
     * @param event The event to be tracked.
     */
    suspend fun trackEvent(event: MonitorEvent)

    /**
     * Sends a performance metric to the monitoring service.
     *
     * @param metric The metric to be recorded.
     */
    suspend fun trackMetric(metric: PerformanceMetric)

    /**
     * Starts a native trace on the provider.
     * @param traceKey Unique name of the trace.
     * @param properties Initial properties.
     */
    suspend fun startTrace(traceKey: String, properties: Map<String, Any>? = null) {}

    /**
     * Stops a native trace on the provider.
     * @param traceKey Unique name of the trace.
     * @param properties Final properties to attach.
     */
    suspend fun stopTrace(traceKey: String, properties: Map<String, Any>? = null) {}

    /**
     * Cancels a native trace on the provider.
     * @param traceKey Unique name of the trace.
     */
    suspend fun cancelTrace(traceKey: String) {}
}
