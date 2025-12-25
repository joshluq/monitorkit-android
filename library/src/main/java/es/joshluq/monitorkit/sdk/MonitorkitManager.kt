package es.joshluq.monitorkit.sdk

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.usecase.AddProviderInput
import es.joshluq.monitorkit.domain.usecase.AddProviderUseCase
import es.joshluq.monitorkit.domain.usecase.CancelTraceInput
import es.joshluq.monitorkit.domain.usecase.CancelTraceUseCase
import es.joshluq.monitorkit.domain.usecase.RemoveProviderInput
import es.joshluq.monitorkit.domain.usecase.RemoveProviderUseCase
import es.joshluq.monitorkit.domain.usecase.StartTraceInput
import es.joshluq.monitorkit.domain.usecase.StartTraceUseCase
import es.joshluq.monitorkit.domain.usecase.StopTraceInput
import es.joshluq.monitorkit.domain.usecase.StopTraceUseCase
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
 */
@Singleton
class MonitorkitManager @Inject constructor(
    private val addProviderUseCase: AddProviderUseCase,
    private val removeProviderUseCase: RemoveProviderUseCase,
    private val trackEventUseCase: TrackEventUseCase,
    private val trackMetricUseCase: TrackMetricUseCase,
    private val startTraceUseCase: StartTraceUseCase,
    private val stopTraceUseCase: StopTraceUseCase,
    private val cancelTraceUseCase: CancelTraceUseCase,
    private val urlSanitizer: UrlSanitizer
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Thread-safe map to store active traces for internal tracking
    private val activeTraces = ConcurrentHashMap<String, TraceContext>()

    // Configuration flag to determine if traces should be handled natively by providers
    private var useNativeTracing: Boolean = false

    /**
     * Internal data holder for active traces.
     */
    private data class TraceContext(
        val startTime: Long,
        val properties: Map<String, Any>?
    )

    /**
     * Enables or disables the use of native provider tracing (e.g., Firebase Trace objects).
     *
     * @param enabled If true, `startTrace` and `stopTrace` calls will be delegated directly to providers.
     *                If false (default), the SDK calculates the duration internally and
     *                sends a [PerformanceMetric.Trace].
     */
    fun setUseNativeTracing(enabled: Boolean) {
        this.useNativeTracing = enabled
    }

    /**
     * Configures the URL patterns for automatic sanitization of Network metrics.
     */
    fun configureUrlPatterns(patterns: List<String>) {
        urlSanitizer.configurePatterns(patterns)
    }

    /**
     * Adds a monitoring provider to the library.
     */
    fun addProvider(provider: MonitorProvider): MonitorkitManager {
        addProviderUseCase(AddProviderInput(provider))
            .launchIn(scope)
        return this
    }

    /**
     * Removes a monitoring provider from the library.
     */
    fun removeProvider(providerKey: String): MonitorkitManager {
        removeProviderUseCase(RemoveProviderInput(providerKey))
            .launchIn(scope)
        return this
    }

    /**
     * Tracks a custom event.
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
     */
    fun trackMetric(
        metric: PerformanceMetric,
        providerKey: String? = null
    ) {
        val processedMetric = if (metric is PerformanceMetric.Network) {
            val sanitizedUrl = urlSanitizer.sanitize(metric.url)
            metric.copy(url = sanitizedUrl)
        } else {
            metric
        }

        trackMetricUseCase(TrackMetricInput(processedMetric, providerKey))
            .launchIn(scope)
    }

    /**
     * Starts a custom trace timer.
     * If [useNativeTracing] is true, delegates to providers.
     * Otherwise, tracks internally.
     */
    fun startTrace(traceKey: String, properties: Map<String, Any>? = null) {
        if (useNativeTracing) {
            startTraceUseCase(StartTraceInput(traceKey, properties)).launchIn(scope)
        } else {
            activeTraces[traceKey] = TraceContext(
                startTime = System.currentTimeMillis(),
                properties = properties
            )
        }
    }

    /**
     * Stops a custom trace.
     * If [useNativeTracing] is true, delegates to providers.
     * Otherwise, calculates duration and sends a metric.
     */
    fun stopTrace(traceKey: String, properties: Map<String, Any>? = null) {
        if (useNativeTracing) {
            stopTraceUseCase(StopTraceInput(traceKey, properties)).launchIn(scope)
        } else {
            val context = activeTraces.remove(traceKey) ?: return

            val duration = System.currentTimeMillis() - context.startTime

            // Merge properties: End properties overwrite start properties on collision
            val mergedProperties = (context.properties.orEmpty() + properties.orEmpty()).takeIf { it.isNotEmpty() }

            val metric = PerformanceMetric.Trace(
                name = traceKey,
                durationMs = duration,
                properties = mergedProperties
            )

            trackMetric(metric)
        }
    }

    /**
     * Cancels an active trace.
     */
    fun cancelTrace(traceKey: String) {
        if (useNativeTracing) {
            cancelTraceUseCase(CancelTraceInput(traceKey)).launchIn(scope)
        } else {
            activeTraces.remove(traceKey)
        }
    }
}
