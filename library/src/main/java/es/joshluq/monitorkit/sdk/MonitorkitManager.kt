package es.joshluq.monitorkit.sdk

import es.joshluq.monitorkit.data.datasource.MonitorDataSourceImpl
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.data.repository.MonitorRepositoryImpl
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.usecase.AddProviderInput
import es.joshluq.monitorkit.domain.usecase.AddProviderUseCase
import es.joshluq.monitorkit.domain.usecase.CancelTraceInput
import es.joshluq.monitorkit.domain.usecase.CancelTraceUseCase
import es.joshluq.monitorkit.domain.usecase.RemoveAttributeInput
import es.joshluq.monitorkit.domain.usecase.RemoveAttributeUseCase
import es.joshluq.monitorkit.domain.usecase.RemoveAttributesInput
import es.joshluq.monitorkit.domain.usecase.RemoveAttributesUseCase
import es.joshluq.monitorkit.domain.usecase.RemoveProviderInput
import es.joshluq.monitorkit.domain.usecase.RemoveProviderUseCase
import es.joshluq.monitorkit.domain.usecase.SetAttributeInput
import es.joshluq.monitorkit.domain.usecase.SetAttributeUseCase
import es.joshluq.monitorkit.domain.usecase.SetAttributesInput
import es.joshluq.monitorkit.domain.usecase.SetAttributesUseCase
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

/**
 * Main entry point for the Monitorkit library.
 * This manager coordinates the monitoring operations and routes them to the registered providers.
 *
 * It uses the Builder pattern for declarative and fluent configuration via Manual Dependency Injection.
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
    private val removeAttributesUseCase: RemoveAttributesUseCase,
    private val urlSanitizer: UrlSanitizer,
    private val useNativeTracing: Boolean,
    initialProviders: List<MonitorProvider>,
    urlPatterns: List<String>
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Thread-safe map to store active traces for internal tracking
    private val activeTraces = ConcurrentHashMap<String, TraceContext>()

    private data class TraceContext(
        val startTime: Long,
        val properties: Map<String, Any>?
    )

    init {
        // 1. Configure URL Sanitization Patterns
        urlSanitizer.configurePatterns(urlPatterns)

        // 2. Register initial providers
        initialProviders.forEach { provider ->
            addProvider(provider)
        }
    }

    /**
     * Adds a monitoring provider to the library at runtime.
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
     * Removes a monitoring provider from the library at runtime.
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
     * Sets a global attribute for all registered providers.
     */
    fun setAttribute(key: String, value: String, providerKey: String? = null): MonitorkitManager {
        setAttributeUseCase(SetAttributeInput(key, value, providerKey))
            .launchIn(scope)
        return this
    }

    /**
     * Sets multiple global attributes for all registered providers.
     */
    fun setAttributes(attributes: Map<String, String>, providerKey: String? = null): MonitorkitManager {
        setAttributesUseCase(SetAttributesInput(attributes, providerKey))
            .launchIn(scope)
        return this
    }

    /**
     * Removes a global attribute from all registered providers.
     */
    fun removeAttribute(key: String, providerKey: String? = null): MonitorkitManager {
        removeAttributeUseCase(RemoveAttributeInput(key, providerKey))
            .launchIn(scope)
        return this
    }

    /**
     * Removes multiple global attributes from all registered providers.
     */
    fun removeAttributes(keys: List<String>, providerKey: String? = null): MonitorkitManager {
        removeAttributesUseCase(RemoveAttributesInput(keys, providerKey))
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
     * Automatically sanitizes URLs if the metric is of type [PerformanceMetric.Network].
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

    /**
     * Builder class for [MonitorkitManager].
     * Provides a fluent API for SDK configuration.
     */
    class Builder {
        private val providers = mutableListOf<MonitorProvider>()
        private var useNativeTracing = false
        private val urlPatterns = mutableListOf<String>()

        /**
         * Adds an initial monitoring provider.
         *
         * @param provider The provider implementation.
         * @return This builder instance.
         */
        fun addProvider(provider: MonitorProvider) = apply {
            providers.add(provider)
        }

        /**
         * Enables or disables native tracing delegation to providers.
         *
         * @param enabled True to use native tracing, false for internal tracking.
         * @return This builder instance.
         */
        fun setUseNativeTracing(enabled: Boolean) = apply {
            useNativeTracing = enabled
        }

        /**
         * Configures the initial URL patterns for automatic sanitization of Network metrics.
         *
         * @param patterns List of path patterns using wildcards (* for segment, ** for suffix).
         * @return This builder instance.
         */
        fun configureUrlPatterns(patterns: List<String>) = apply {
            urlPatterns.addAll(patterns)
        }

        /**
         * Builds and returns the [MonitorkitManager] instance.
         *
         * @return A configured [MonitorkitManager].
         */
        fun build(): MonitorkitManager {
            val dataSource = MonitorDataSourceImpl()
            val repository = MonitorRepositoryImpl(dataSource)
            val urlSanitizer = UrlSanitizer()

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
                removeAttributesUseCase = RemoveAttributesUseCase(repository),
                urlSanitizer = urlSanitizer,
                useNativeTracing = useNativeTracing,
                initialProviders = providers,
                urlPatterns = urlPatterns
            )
        }
    }
}
