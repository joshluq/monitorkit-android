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
                "Network Metric: ${metric.method} ${metric.url} [${metric.statusCode}] | Response Time: ${metric.responseTime}ms"
            is PerformanceMetric.ScreenLoad -> 
                "Screen Metric: ${metric.screenName} | Load Time: ${metric.loadTime}ms"
            is PerformanceMetric.Trace ->
                "Trace Metric (INTERNAL): ${metric.name} | Duration: ${metric.durationMs}ms | Properties: ${metric.properties}"
        }
        Log.d("Monitorkit-Showcase", logMessage)
    }

    override fun setAttribute(key: String, value: String) {
        Log.d("Monitorkit-Showcase", "Attribute SET: $key = $value")
    }

    override fun removeAttribute(key: String) {
        Log.d("Monitorkit-Showcase", "Attribute REMOVED: $key")
    }

    override suspend fun startTrace(traceKey: String, properties: Map<String, Any>?) {
        Log.d("Monitorkit-Showcase", "Native Trace STARTED: $traceKey | Props: $properties")
    }

    override suspend fun stopTrace(traceKey: String, properties: Map<String, Any>?) {
        Log.d("Monitorkit-Showcase", "Native Trace STOPPED: $traceKey | Props: $properties")
    }

    override suspend fun cancelTrace(traceKey: String) {
        Log.d("Monitorkit-Showcase", "Native Trace CANCELLED: $traceKey")
    }
}
