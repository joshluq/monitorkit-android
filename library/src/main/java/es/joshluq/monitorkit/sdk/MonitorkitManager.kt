package es.joshluq.monitorkit.sdk

import android.util.Log
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.usecase.AddProviderInput
import es.joshluq.monitorkit.domain.usecase.AddProviderUseCase
import es.joshluq.monitorkit.domain.usecase.RemoveProviderInput
import es.joshluq.monitorkit.domain.usecase.RemoveProviderUseCase
import es.joshluq.monitorkit.domain.usecase.TrackEventInput
import es.joshluq.monitorkit.domain.usecase.TrackEventUseCase
import es.joshluq.monitorkit.domain.usecase.TrackMetricInput
import es.joshluq.monitorkit.domain.usecase.TrackMetricUseCase
import es.joshluq.monitorkit.sdk.sanitizer.UrlSanitizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main entry point for the Monitorkit library.
 * This manager coordinates the monitoring operations and routes them to the registered providers.
 *
 * @property addProviderUseCase Use case for adding providers.
 * @property removeProviderUseCase Use case for removing providers.
 * @property trackEventUseCase Use case for tracking events.
 * @property trackMetricUseCase Use case for tracking metrics.
 * @property urlSanitizer Utility for sanitizing URLs in network metrics.
 */
@Singleton
class MonitorkitManager @Inject constructor(
    private val addProviderUseCase: AddProviderUseCase,
    private val removeProviderUseCase: RemoveProviderUseCase,
    private val trackEventUseCase: TrackEventUseCase,
    private val trackMetricUseCase: TrackMetricUseCase,
    private val urlSanitizer: UrlSanitizer
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Thread-safe map to store active traces
    private val activeTraces = ConcurrentHashMap<String, TraceContext>()

    /**
     * Internal data holder for active traces.
     */
    private data class TraceContext(
        val startTime: Long,
        val properties: Map<String, Any>?
    )

    /**
     * Configures the URL patterns for automatic sanitization of Network metrics.
     *
     * @param patterns List of path patterns using wildcards (`*` for segment, `**` for suffix).
     * Example: listOf("api/users/`*`/profile", "auth/`**`")
     **/
    fun configureUrlPatterns(patterns: List<String>) {
        urlSanitizer.configurePatterns(patterns)
    }

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
     * Removes a monitoring provider from the library.
     *
     * @param providerKey The unique key of the provider to remove.
     * @return The [MonitorkitManager] instance for fluent API usage.
     */
    fun removeProvider(providerKey: String): MonitorkitManager {
        removeProviderUseCase(RemoveProviderInput(providerKey))
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
     * Tracks a performance metric.
     * Automatically sanitizes URLs if the metric is of type [PerformanceMetric.Network].
     *
     * @param metric The performance metric to record (Resource, Network, ScreenLoad, Trace).
     * @param providerKey Optional key to target a specific provider.
     */
    fun trackMetric(
        metric: PerformanceMetric,
        providerKey: String? = null
    ) {
        val processedMetric = when (metric) {
            is PerformanceMetric.Network -> {
                val sanitizedUrl = urlSanitizer.sanitize(metric.url)
                metric.copy(url = sanitizedUrl)
            }

            else -> metric
        }

        trackMetricUseCase(TrackMetricInput(processedMetric, providerKey))
            .launchIn(scope)
    }

    /**
     * Starts a custom trace timer.
     * If the key already exists, it restarts the timer.
     *
     * @param traceKey Unique identifier for the trace (e.g., "image_processing").
     * @param properties Optional initial properties.
     */
    fun startTrace(traceKey: String, properties: Map<String, Any>? = null) {
        activeTraces[traceKey] = TraceContext(
            startTime = System.currentTimeMillis(),
            properties = properties
        )
    }

    /**
     * Stops a custom trace, calculates duration, and sends the metric.
     *
     * @param traceKey Unique identifier for the trace to stop.
     * @param properties Optional properties to add/overwrite at the end of the trace.
     */
    fun stopTrace(traceKey: String, properties: Map<String, Any>? = null) {
        val context = activeTraces.remove(traceKey)
        
        if (context == null) {
            Log.w("Monitorkit", "Attempted to stop trace '$traceKey' but it was not active.")
            return
        }

        val duration = System.currentTimeMillis() - context.startTime
        
        val mergedProperties = (context.properties.orEmpty() + properties.orEmpty()).takeIf { it.isNotEmpty() }

        val metric = PerformanceMetric.Trace(
            name = traceKey,
            durationMs = duration,
            properties = mergedProperties
        )

        trackMetric(metric)
    }

    /**
     * Cancels an active trace without sending any metric.
     * Useful if the traced process failed or was aborted.
     *
     * @param traceKey Unique identifier for the trace to cancel.
     */
    fun cancelTrace(traceKey: String) {
        activeTraces.remove(traceKey)
    }
}
