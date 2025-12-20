package es.joshluq.monitorkit.showcase

import android.util.Log
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric

/**
 * A simple implementation of [MonitorProvider] that logs events and metrics to Logcat.
 * This represents how a consumer application would integrate a third-party service.
 */
class LogMonitorProvider(override val key: String = "LOGCAT") : MonitorProvider {

    override suspend fun trackEvent(event: MonitorEvent) {
        Log.d("Monitorkit-Showcase", "Event Tracked: ${event.name} | Properties: ${event.properties}")
    }

    override suspend fun trackMetric(metric: PerformanceMetric) {
        val logMessage = when (metric) {
            is PerformanceMetric.Resource -> 
                "Resource Metric: ${metric.type} | Value: ${metric.value}${metric.unit}"
            is PerformanceMetric.Network -> 
                "Network Metric: ${metric.method} ${metric.url} | Response Time: ${metric.responseTime}ms"
            is PerformanceMetric.ScreenLoad -> 
                "Screen Metric: ${metric.screenName} | Load Time: ${metric.loadTime}ms"
        }
        Log.d("Monitorkit-Showcase", logMessage)
    }
}
