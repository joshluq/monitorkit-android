package es.joshluq.monitorkit.data.datasource

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [MonitorDataSource].
 * Manages a collection of providers and routes data to them.
 * Optimized for high-frequency reads and low-frequency writes using [CopyOnWriteArrayList].
 */
@Singleton
class MonitorDataSourceImpl @Inject constructor() : MonitorDataSource {

    private val providers = CopyOnWriteArrayList<MonitorProvider>()

    override fun addProvider(provider: MonitorProvider) {
        providers.add(provider)
    }

    override fun removeProvider(providerKey: String) {
        providers.removeIf { it.key == providerKey }
    }

    override suspend fun trackEvent(event: MonitorEvent, providerKey: String?) {
        getProviders(providerKey).forEach { it.trackEvent(event) }
    }

    override suspend fun trackMetric(metric: PerformanceMetric, providerKey: String?) {
        getProviders(providerKey).forEach { it.trackMetric(metric) }
    }

    override suspend fun startTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?) {
        getProviders(providerKey).forEach { it.startTrace(traceKey, properties) }
    }

    override suspend fun stopTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?) {
        getProviders(providerKey).forEach { it.stopTrace(traceKey, properties) }
    }

    override suspend fun cancelTrace(traceKey: String, providerKey: String?) {
        getProviders(providerKey).forEach { it.cancelTrace(traceKey) }
    }

    /**
     * Filters the providers based on the key.
     * If a key is provided, only the matching provider is returned.
     * If no key is provided, all providers are returned (default behavior).
     */
    private fun getProviders(key: String?): List<MonitorProvider> {
        return if (key != null) {
            providers.filter { it.key == key }
        } else {
            providers
        }
    }
}
