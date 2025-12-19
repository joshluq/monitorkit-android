package es.joshluq.monitorkit.data.datasource

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric

/**
 * Data source interface for monitoring operations.
 * It abstracts how events and metrics are dispatched to the providers.
 */
interface MonitorDataSource {
    /**
     * Adds a provider to the data source.
     *
     * @param provider The provider to add.
     */
    fun addProvider(provider: MonitorProvider)

    /**
     * Dispatches a custom event.
     *
     * @param event The event to be tracked.
     * @param providerKey Optional key to target a specific provider.
     */
    suspend fun trackEvent(event: MonitorEvent, providerKey: String? = null)

    /**
     * Dispatches a performance metric.
     *
     * @param metric The performance metric to record.
     * @param providerKey Optional key to target a specific provider.
     */
    suspend fun trackMetric(metric: PerformanceMetric, providerKey: String? = null)
}
