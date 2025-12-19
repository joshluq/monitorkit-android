package es.joshluq.monitorkit.domain.repository

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric

/**
 * Repository interface for monitoring operations.
 * Defines the contract for tracking events and collecting system metrics.
 */
interface MonitorRepository {
    /**
     * Adds a provider to the repository.
     *
     * @param provider The provider to add.
     */
    fun addProvider(provider: MonitorProvider)

    /**
     * Tracks a custom event.
     *
     * @param event The event to be tracked.
     * @param providerKey Optional key to target a specific provider.
     */
    suspend fun trackEvent(event: MonitorEvent, providerKey: String? = null)

    /**
     * Records a performance metric.
     *
     * @param metric The performance metric to record.
     * @param providerKey Optional key to target a specific provider.
     */
    suspend fun trackMetric(metric: PerformanceMetric, providerKey: String? = null)
}
