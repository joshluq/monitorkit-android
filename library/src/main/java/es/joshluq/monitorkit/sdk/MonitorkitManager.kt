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
import kotlinx.coroutines.flow.launchIn
import java.util.concurrent.ConcurrentHashMap

/**
 * Main entry point for the Monitorkit library.
 * This manager coordinates the monitoring operations and routes them to the registered providers.
 *
 * It uses the Builder pattern for declarative and fluent configuration via [Manual Dependency Injection].
 *
 * @param builder The [Builder] instance containing the configuration.
 */
class MonitorkitManager private constructor(builder: Builder) {

    private val addProviderUseCase = builder.addProviderUseCase
    private val removeProviderUseCase = builder.removeProviderUseCase
    private val trackEventUseCase = builder.trackEventUseCase
    private val trackMetricUseCase = builder.trackMetricUseCase
    private val startTraceUseCase = builder.startTraceUseCase
    private val stopTraceUseCase = builder.stopTraceUseCase
    private val cancelTraceUseCase = builder.cancelTraceUseCase
    private val setAttributeUseCase = builder.setAttributeUseCase
    private val setAttributesUseCase = builder.setAttributesUseCase
    private val removeAttributeUseCase = builder.removeAttributeUseCase
    private val removeAttributesUseCase = builder.removeAttributesUseCase
    private val urlSanitizer = builder.urlSanitizer

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val activeTraces = ConcurrentHashMap<String, TraceContext>()
    
    private val useNativeTracing: Boolean = builder.useNativeTracing

    private data class TraceContext(
        val startTime: Long,
        val properties: Map<String, Any>?
    )

    init {
        urlSanitizer.configurePatterns(builder.urlPatterns)
        builder.providers.forEach { addProvider(it) }
    }

    /**
     * Adds a monitoring provider to the library at runtime.
     */
    fun addProvider(provider: MonitorProvider): MonitorkitManager {
        addProviderUseCase(AddProviderInput(provider)).launchIn(scope)
        return this
    }

    /**
     * Removes a monitoring provider from the library at runtime.
     */
    fun removeProvider(providerKey: String): MonitorkitManager {
        removeProviderUseCase(RemoveProviderInput(providerKey)).launchIn(scope)
        return this
    }

    /**
     * Sets a global attribute for all registered providers.
     */
    fun setAttribute(key: String, value: String, providerKey: String? = null): MonitorkitManager {
        setAttributeUseCase(SetAttributeInput(key, value, providerKey)).launchIn(scope)
        return this
    }

    /**
     * Sets multiple global attributes for all registered providers.
     */
    fun setAttributes(attributes: Map<String, String>, providerKey: String? = null): MonitorkitManager {
        setAttributesUseCase(SetAttributesInput(attributes, providerKey)).launchIn(scope)
        return this
    }

    /**
     * Removes a global attribute from all registered providers.
     */
    fun removeAttribute(key: String, providerKey: String? = null): MonitorkitManager {
        removeAttributeUseCase(RemoveAttributeInput(key, providerKey)).launchIn(scope)
        return this
    }

    /**
     * Removes multiple global attributes from all registered providers.
     */
    fun removeAttributes(keys: List<String>, providerKey: String? = null): MonitorkitManager {
        removeAttributesUseCase(RemoveAttributesInput(keys, providerKey)).launchIn(scope)
        return this
    }

    /**
     * Tracks a custom event.
     */
    fun trackEvent(name: String, properties: Map<String, Any> = emptyMap(), providerKey: String? = null) {
        val event = MonitorEvent(name, properties)
        trackEventUseCase(TrackEventInput(event, providerKey)).launchIn(scope)
    }

    /**
     * Tracks a performance metric.
     */
    fun trackMetric(metric: PerformanceMetric, providerKey: String? = null) {
        val processedMetric = if (metric is PerformanceMetric.Network) {
            val sanitizedUrl = urlSanitizer.sanitize(metric.url)
            metric.copy(url = sanitizedUrl)
        } else {
            metric
        }
        trackMetricUseCase(TrackMetricInput(processedMetric, providerKey)).launchIn(scope)
    }

    /**
     * Starts a custom trace timer.
     */
    fun startTrace(traceKey: String, properties: Map<String, Any>? = null) {
        if (useNativeTracing) {
            startTraceUseCase(StartTraceInput(traceKey, properties)).launchIn(scope)
        } else {
            activeTraces[traceKey] = TraceContext(System.currentTimeMillis(), properties)
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
            val mergedProperties = (context.properties.orEmpty() + properties.orEmpty()).takeIf { it.isNotEmpty() }
            trackMetric(PerformanceMetric.Trace(traceKey, duration, mergedProperties))
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
     * Instantiates all internal dependencies manually to remain framework-agnostic.
     */
    class Builder {
        internal val providers = mutableListOf<MonitorProvider>()
        internal var useNativeTracing = false
        internal val urlPatterns = mutableListOf<String>()

        // Manual Dependency Injection
        private val dataSource = MonitorDataSourceImpl()
        private val repository = MonitorRepositoryImpl(dataSource)
        
        internal val addProviderUseCase = AddProviderUseCase(repository)
        internal val removeProviderUseCase = RemoveProviderUseCase(repository)
        internal val trackEventUseCase = TrackEventUseCase(repository)
        internal val trackMetricUseCase = TrackMetricUseCase(repository)
        internal val startTraceUseCase = StartTraceUseCase(repository)
        internal val stopTraceUseCase = StopTraceUseCase(repository)
        internal val cancelTraceUseCase = CancelTraceUseCase(repository)
        internal val setAttributeUseCase = SetAttributeUseCase(repository)
        internal val setAttributesUseCase = SetAttributesUseCase(repository)
        internal val removeAttributeUseCase = RemoveAttributeUseCase(repository)
        internal val removeAttributesUseCase = RemoveAttributesUseCase(repository)
        internal val urlSanitizer = UrlSanitizer()

        /**
         * Adds an initial monitoring provider.
         */
        fun addProvider(provider: MonitorProvider) = apply { providers.add(provider) }

        /**
         * Enables or disables native tracing delegation.
         */
        fun setUseNativeTracing(enabled: Boolean) = apply { useNativeTracing = enabled }

        /**
         * Configures the initial URL patterns for sanitization.
         */
        fun configureUrlPatterns(patterns: List<String>) = apply { urlPatterns.addAll(patterns) }

        /**
         * Builds and returns the [MonitorkitManager] instance.
         */
        fun build(): MonitorkitManager = MonitorkitManager(this)
    }
}
