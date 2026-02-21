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
        forEachProvider(providerKey) { it.trackEvent(event) }
    }

    override suspend fun trackMetric(metric: PerformanceMetric, providerKey: String?) {
        forEachProvider(providerKey) { it.trackMetric(metric) }
    }

    override fun setAttribute(key: String, value: String, providerKey: String?) {
        forEachProvider(providerKey) { it.setAttribute(key, value) }
    }

    override fun setAttributes(attributes: Map<String, String>, providerKey: String?) {
        forEachProvider(providerKey) { it.setAttributes(attributes) }
    }

    override fun removeAttribute(key: String, providerKey: String?) {
        forEachProvider(providerKey) { it.removeAttribute(key) }
    }

    override fun removeAttributes(keys: List<String>, providerKey: String?) {
        forEachProvider(providerKey) { it.removeAttributes(keys) }
    }

    override suspend fun startTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?) {
        forEachProvider(providerKey) { it.startTrace(traceKey, properties) }
    }

    override suspend fun stopTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?) {
        forEachProvider(providerKey) { it.stopTrace(traceKey, properties) }
    }

    override suspend fun cancelTrace(traceKey: String, providerKey: String?) {
        forEachProvider(providerKey) { it.cancelTrace(traceKey) }
    }

    /**
     * Iterates over providers matching the key, or all providers if key is null.
     * Inline to avoid lambda allocation and allow direct iteration.
     */
    private inline fun forEachProvider(key: String?, action: (MonitorProvider) -> Unit) {
        if (key != null) {
            for (provider in providers) {
                if (provider.key == key) {
                    action(provider)
                }
            }
        } else {
            for (provider in providers) {
                action(provider)
            }
        }
    }
}
