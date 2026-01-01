package es.joshluq.monitorkit.data.datasource

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric

/**
 * Data source interface for monitoring operations.
 * It abstracts how events and metrics are dispatched to the providers.
 */
internal interface MonitorDataSource {
    /**
     * Adds a provider to the data source.
     */
    fun addProvider(provider: MonitorProvider)

    /**
     * Removes a provider from the data source by its key.
     */
    fun removeProvider(providerKey: String)

    /**
     * Dispatches a custom event.
     */
    suspend fun trackEvent(event: MonitorEvent, providerKey: String? = null)

    /**
     * Dispatches a performance metric.
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
     * Dispatches a start trace operation to providers.
     */
    suspend fun startTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?)

    /**
     * Dispatches a stop trace operation to providers.
     */
    suspend fun stopTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?)

    /**
     * Dispatches a cancel trace operation to providers.
     */
    suspend fun cancelTrace(traceKey: String, providerKey: String?)
}
