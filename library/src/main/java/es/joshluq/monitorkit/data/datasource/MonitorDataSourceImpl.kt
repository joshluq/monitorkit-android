package es.joshluq.monitorkit.data.datasource

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Implementation of [MonitorDataSource].
 * Manages a collection of providers and routes data to them.
 * Optimized for high-frequency reads and low-frequency writes using [CopyOnWriteArrayList].
 */
internal class MonitorDataSourceImpl : MonitorDataSource {

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

    override fun setAttribute(key: String, value: String, providerKey: String?) {
        getProviders(providerKey).forEach { it.setAttribute(key, value) }
    }

    override fun setAttributes(attributes: Map<String, String>, providerKey: String?) {
        getProviders(providerKey).forEach { it.setAttributes(attributes) }
    }

    override fun removeAttribute(key: String, providerKey: String?) {
        getProviders(providerKey).forEach { it.removeAttribute(key) }
    }

    override fun removeAttributes(keys: List<String>, providerKey: String?) {
        getProviders(providerKey).forEach { it.removeAttributes(keys) }
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

    private fun getProviders(key: String?): List<MonitorProvider> {
        return if (key != null) {
            providers.filter { it.key == key }
        } else {
            providers
        }
    }
}
