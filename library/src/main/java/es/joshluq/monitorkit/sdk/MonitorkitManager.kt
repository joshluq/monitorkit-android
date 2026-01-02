package es.joshluq.monitorkit.sdk

import es.joshluq.monitorkit.data.datasource.MonitorDataSourceImpl
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.data.repository.MonitorRepositoryImpl
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.usecase.*
import es.joshluq.monitorkit.sdk.sanitizer.UrlSanitizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Main entry point for the Monitorkit library.
 * This manager coordinates monitoring operations and routes them to the registered providers.
 *
 * It uses the Builder pattern for declarative and fluent configuration via Manual Dependency Injection.
 * All operations are thread-safe and executed asynchronously in the [Dispatchers.IO] scope.
 */
class MonitorkitManager internal constructor(
    private val addProviderUseCase: AddProviderUseCase,
    private val removeProviderUseCase: RemoveProviderUseCase,
    private val trackEventUseCase: TrackEventUseCase,
    private val trackMetricUseCase: TrackMetricUseCase,
    private val startTraceUseCase: StartTraceUseCase,
    private val stopTraceUseCase: StopTraceUseCase,
    private val cancelTraceUseCase: CancelTraceUseCase,
    private val setAttributeUseCase: SetAttributeUseCase,
    private val setAttributesUseCase: SetAttributesUseCase,
    private val removeAttributeUseCase: RemoveAttributeUseCase,
    private val removeAttributesUseCase: RemoveAttributesUseCase
) {

    internal val urlSanitizer: UrlSanitizer = UrlSanitizer()
    internal var useNativeTracing: Boolean = false

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activeTraces = ConcurrentHashMap<String, TraceContext>()

    private data class TraceContext(
        val startTime: Long,
        val properties: Map<String, Any>?
    )

    /**
     * Adds a monitoring provider to the library at runtime.
     *
     * @param provider The [MonitorProvider] implementation to add.
     */
    fun addProvider(provider: MonitorProvider) {
        scope.launch {
            addProviderUseCase(AddProviderInput(provider))
        }
    }

    /**
     * Removes a monitoring provider from the library at runtime.
     *
     * @param providerKey The unique key of the provider to remove.
     */
    fun removeProvider(providerKey: String) {
        scope.launch {
            removeProviderUseCase(RemoveProviderInput(providerKey))
        }
    }

    /**
     * Sets a global attribute for registered providers.
     *
     * @param key The attribute key (e.g., "user_id").
     * @param value The attribute value.
     * @param providerKey Optional. If provided, the attribute will only be set for that specific provider.
     */
    fun setAttribute(key: String, value: String, providerKey: String? = null) {
        scope.launch {
            setAttributeUseCase(SetAttributeInput(key, value, providerKey))
        }
    }

    /**
     * Sets multiple global attributes for registered providers.
     *
     * @param attributes A map of key-value pairs to set.
     * @param providerKey Optional. If provided, the attributes will only be set for that specific provider.
     */
    fun setAttributes(attributes: Map<String, String>, providerKey: String? = null) {
        scope.launch {
            setAttributesUseCase(SetAttributesInput(attributes, providerKey))
        }
    }

    /**
     * Removes a global attribute from registered providers.
     *
     * @param key The attribute key to remove.
     * @param providerKey Optional. If provided, the attribute will only be removed from that specific provider.
     */
    fun removeAttribute(key: String, providerKey: String? = null) {
        scope.launch {
            removeAttributeUseCase(RemoveAttributeInput(key, providerKey))
        }
    }

    /**
     * Removes multiple global attributes from registered providers.
     *
     * @param keys The list of attribute keys to remove.
     * @param providerKey Optional. If provided, the attributes will only be removed from that specific provider.
     */
    fun removeAttributes(keys: List<String>, providerKey: String? = null) {
        scope.launch {
            removeAttributesUseCase(RemoveAttributesInput(keys, providerKey))
        }
    }

    /**
     * Tracks a custom business event.
     *
     * @param name Unique name identifying the event.
     * @param properties Optional metadata associated with the event.
     * @param providerKey Optional. If provided, the event will only be sent to that specific provider.
     */
    fun trackEvent(name: String, properties: Map<String, Any> = emptyMap(), providerKey: String? = null) {
        val event = MonitorEvent(name, properties)
        scope.launch {
            trackEventUseCase(TrackEventInput(event, providerKey))
        }
    }

    /**
     * Tracks a performance metric.
     *
     * If the metric is [PerformanceMetric.Network], the URL is automatically sanitized
     * based on the configured patterns before being sent to providers.
     *
     * @param metric The [PerformanceMetric] to record (Resource, Network, ScreenLoad, or Trace).
     * @param providerKey Optional. If provided, the metric will only be sent to that specific provider.
     */
    fun trackMetric(metric: PerformanceMetric, providerKey: String? = null) {
        val processedMetric = if (metric is PerformanceMetric.Network) {
            val sanitizedUrl = urlSanitizer.sanitize(metric.url)
            metric.copy(url = sanitizedUrl)
        } else {
            metric
        }
        scope.launch {
            trackMetricUseCase(TrackMetricInput(processedMetric, providerKey))
        }
    }

    /**
     * Starts a custom trace timer.
     *
     * If `useNativeTracing` is true, the start signal is delegated to all providers.
     * Otherwise, the SDK starts an internal timer.
     *
     * @param traceKey Unique identifier for the trace (e.g., "image_upload").
     * @param properties Optional initial metadata for the trace.
     */
    fun startTrace(traceKey: String, properties: Map<String, Any>? = null) {
        if (useNativeTracing) {
            scope.launch {
                startTraceUseCase(StartTraceInput(traceKey, properties))
            }
        } else {
            activeTraces[traceKey] = TraceContext(System.currentTimeMillis(), properties)
        }
    }

    /**
     * Stops a custom trace and records its duration.
     *
     * If `useNativeTracing` is true, the stop signal is delegated to providers.
     * Otherwise, the SDK calculates the duration and reports a [PerformanceMetric.Trace].
     *
     * @param traceKey Unique identifier for the trace.
     * @param properties Optional final metadata to merge with the initial properties.
     */
    fun stopTrace(traceKey: String, properties: Map<String, Any>? = null) {
        if (useNativeTracing) {
            scope.launch {
                stopTraceUseCase(StopTraceInput(traceKey, properties))
            }
        } else {
            val context = activeTraces.remove(traceKey) ?: return
            val duration = System.currentTimeMillis() - context.startTime
            val mergedProperties = (context.properties.orEmpty() + properties.orEmpty()).takeIf { it.isNotEmpty() }
            trackMetric(PerformanceMetric.Trace(traceKey, duration, mergedProperties))
        }
    }

    /**
     * Cancels an active trace without reporting any metric.
     *
     * @param traceKey Unique identifier for the trace to cancel.
     */
    fun cancelTrace(traceKey: String) {
        if (useNativeTracing) {
            scope.launch {
                cancelTraceUseCase(CancelTraceInput(traceKey))
            }
        } else {
            activeTraces.remove(traceKey)
        }
    }

    /**
     * Builder class for [MonitorkitManager].
     * Instantiates all internal dependencies manually to remain framework-agnostic.
     */
    class Builder {
        private val providers = mutableListOf<MonitorProvider>()
        private var useNativeTracing = false
        private val urlPatterns = mutableListOf<String>()

        /**
         * Adds an initial monitoring provider (e.g., Firebase, Sentry, Logcat).
         *
         * @param provider The [MonitorProvider] implementation.
         */
        fun addProvider(provider: MonitorProvider) = apply { providers.add(provider) }

        /**
         * Configures whether the SDK should delegate trace lifecycle (start/stop)
         * directly to the providers (Native Mode) or calculate durations internally (Internal Mode).
         *
         * @param enabled True for Native Mode, false for Internal Mode (default).
         */
        fun setUseNativeTracing(enabled: Boolean) = apply { useNativeTracing = enabled }

        /**
         * Configures URL patterns for the [UrlSanitizer].
         *
         * Patterns support wildcards:
         * - `*`: matches a single path segment.
         * - `**`: matches everything until the end of the URL.
         *
         * @param patterns List of path patterns to allowlist.
         */
        fun configureUrlPatterns(patterns: List<String>) = apply { urlPatterns.addAll(patterns) }

        /**
         * Builds and returns the [MonitorkitManager] instance with the specified configuration.
         *
         * @return A fully initialized [MonitorkitManager].
         */
        fun build(): MonitorkitManager {
            val dataSource = MonitorDataSourceImpl()
            val repository = MonitorRepositoryImpl(dataSource)

            return MonitorkitManager(
                addProviderUseCase = AddProviderUseCase(repository),
                removeProviderUseCase = RemoveProviderUseCase(repository),
                trackEventUseCase = TrackEventUseCase(repository),
                trackMetricUseCase = TrackMetricUseCase(repository),
                startTraceUseCase = StartTraceUseCase(repository),
                stopTraceUseCase = StopTraceUseCase(repository),
                cancelTraceUseCase = CancelTraceUseCase(repository),
                setAttributeUseCase = SetAttributeUseCase(repository),
                setAttributesUseCase = SetAttributesUseCase(repository),
                removeAttributeUseCase = RemoveAttributeUseCase(repository),
                removeAttributesUseCase = RemoveAttributesUseCase(repository)
            ).also { manager ->
                manager.useNativeTracing = useNativeTracing
                manager.urlSanitizer.configurePatterns(urlPatterns)
                providers.forEach(manager::addProvider)
            }
        }
    }
}
