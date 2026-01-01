package es.joshluq.monitorkit.data.repository

import es.joshluq.monitorkit.data.datasource.MonitorDataSource
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.repository.MonitorRepository

/**
 * Implementation of [MonitorRepository].
 * Acts as a bridge between the domain layer and the data sources.
 */
internal class MonitorRepositoryImpl(
    private val dataSource: MonitorDataSource
) : MonitorRepository {

    override fun addProvider(provider: MonitorProvider) {
        dataSource.addProvider(provider)
    }

    override fun removeProvider(providerKey: String) {
        dataSource.removeProvider(providerKey)
    }

    override suspend fun trackEvent(event: MonitorEvent, providerKey: String?) {
        dataSource.trackEvent(event, providerKey)
    }

    override suspend fun trackMetric(metric: PerformanceMetric, providerKey: String?) {
        dataSource.trackMetric(metric, providerKey)
    }

    override fun setAttribute(key: String, value: String, providerKey: String?) {
        dataSource.setAttribute(key, value, providerKey)
    }

    override fun setAttributes(attributes: Map<String, String>, providerKey: String?) {
        dataSource.setAttributes(attributes, providerKey)
    }

    override fun removeAttribute(key: String, providerKey: String?) {
        dataSource.removeAttribute(key, providerKey)
    }

    override fun removeAttributes(keys: List<String>, providerKey: String?) {
        dataSource.removeAttributes(keys, providerKey)
    }

    override suspend fun startTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?) {
        dataSource.startTrace(traceKey, properties, providerKey)
    }

    override suspend fun stopTrace(traceKey: String, properties: Map<String, Any>?, providerKey: String?) {
        dataSource.stopTrace(traceKey, properties, providerKey)
    }

    override suspend fun cancelTrace(traceKey: String, providerKey: String?) {
        dataSource.cancelTrace(traceKey, providerKey)
    }
}
