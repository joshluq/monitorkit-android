package es.joshluq.monitorkit.sdk

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MetricType
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.usecase.AddProviderInput
import es.joshluq.monitorkit.domain.usecase.AddProviderUseCase
import es.joshluq.monitorkit.domain.usecase.TrackEventInput
import es.joshluq.monitorkit.domain.usecase.TrackEventUseCase
import es.joshluq.monitorkit.domain.usecase.TrackMetricInput
import es.joshluq.monitorkit.domain.usecase.TrackMetricUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main entry point for the Monitorkit library.
 * This manager coordinates the monitoring operations and routes them to the registered providers.
 *
 * @property addProviderUseCase Use case for adding providers.
 * @property trackEventUseCase Use case for tracking events.
 * @property trackMetricUseCase Use case for tracking metrics.
 */
@Singleton
class MonitorkitManager @Inject constructor(
    private val addProviderUseCase: AddProviderUseCase,
    private val trackEventUseCase: TrackEventUseCase,
    private val trackMetricUseCase: TrackMetricUseCase
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Adds a monitoring provider to the library.
     *
     * @param provider The [MonitorProvider] implementation to add.
     * @return The [MonitorkitManager] instance for fluent API usage.
     */
    fun addProvider(provider: MonitorProvider): MonitorkitManager {
        addProviderUseCase(AddProviderInput(provider))
            .launchIn(scope)
        return this
    }

    /**
     * Tracks a custom event.
     *
     * @param name The name of the event.
     * @param properties Additional data for the event.
     * @param providerKey Optional key to target a specific provider.
     */
    fun trackEvent(
        name: String,
        properties: Map<String, Any> = emptyMap(),
        providerKey: String? = null
    ) {
        val event = MonitorEvent(name, properties)
        trackEventUseCase(TrackEventInput(event, providerKey))
            .launchIn(scope)
    }

    /**
     * Tracks a system performance metric.
     *
     * @param type The type of metric (CPU, MEMORY).
     * @param value The value of the metric.
     * @param unit The unit of measurement.
     * @param providerKey Optional key to target a specific provider.
     */
    fun trackMetric(
        type: MetricType,
        value: Double,
        unit: String,
        providerKey: String? = null
    ) {
        val metric = PerformanceMetric(type, value, unit)
        trackMetricUseCase(TrackMetricInput(metric, providerKey))
            .launchIn(scope)
    }
}
