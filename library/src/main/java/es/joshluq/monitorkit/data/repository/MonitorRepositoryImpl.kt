package es.joshluq.monitorkit.data.repository

import es.joshluq.monitorkit.data.datasource.MonitorDataSource
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import javax.inject.Inject

/**
 * Implementation of [MonitorRepository].
 * Acts as a bridge between the domain layer and the data sources.
 *
 * @property dataSource The data source used to dispatch events and metrics.
 */
class MonitorRepositoryImpl @Inject constructor(
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
