package es.joshluq.monitorkit.domain.repository

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric

/**
 * Repository interface for monitoring operations.
 * Defines the contract for tracking events and collecting system metrics.
 */
internal interface MonitorRepository {
    /**
     * Adds a provider to the repository.
     */
    fun addProvider(provider: MonitorProvider)

    /**
     * Removes a provider from the repository by its key.
     */
    fun removeProvider(providerKey: String)

    /**
     * Tracks a custom event.
     */
    suspend fun trackEvent(event: MonitorEvent, providerKey: String? = null)

    /**
     * Records a performance metric.
     */
    suspend fun trackMetric(metric: PerformanceMetric, providerKey: String? = null)

    /**
     * Sets a global attribute for providers.
     */
    fun setAttribute(key: String, value: String, providerKey: String? = null)

    /**
     * Sets multiple global attributes for providers.
     */
    fun setAttributes(attributes: Map<String, String>, providerKey: String? = null)

    /**
     * Removes a global attribute from providers.
     */
    fun removeAttribute(key: String, providerKey: String? = null)

    /**
     * Removes multiple global attributes from providers.
     */
    fun removeAttributes(keys: List<String>, providerKey: String? = null)

    /**
     * Starts a native trace on the registered providers.
     */
    suspend fun startTrace(traceKey: String, properties: Map<String, Any>? = null, providerKey: String? = null)

    /**
     * Stops a native trace on the registered providers.
     */
    suspend fun stopTrace(traceKey: String, properties: Map<String, Any>? = null, providerKey: String? = null)

    /**
     * Cancels a native trace on the registered providers.
     */
    suspend fun cancelTrace(traceKey: String, providerKey: String? = null)
}
