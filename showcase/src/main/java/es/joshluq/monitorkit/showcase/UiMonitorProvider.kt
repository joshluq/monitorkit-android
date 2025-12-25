package es.joshluq.monitorkit.showcase

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A UI-specific implementation of [MonitorProvider] that enables real-time visualization
 * of monitoring data within the application.
 *
 * Instead of sending data to an external service, it emits events and metrics into a
 * [SharedFlow] that can be consumed by UI components (like a ViewModel).
 *
 * @property key Unique identifier for this provider.
 */
@Singleton
class UiMonitorProvider @Inject constructor() : MonitorProvider {

    override val key: String = "UI_CONSOLE"

    private val _metricsFlow = MutableSharedFlow<ConsoleMessage>(extraBufferCapacity = 100)
    
    /**
     * Flow of console messages containing formatted monitoring data.
     */
    val metricsFlow: SharedFlow<ConsoleMessage> = _metricsFlow.asSharedFlow()

    override suspend fun trackEvent(event: MonitorEvent) {
        _metricsFlow.emit(
            ConsoleMessage(
                type = MessageType.EVENT,
                text = "EVENT: ${event.name} | Props: ${event.properties}"
            )
        )
    }

    override suspend fun trackMetric(metric: PerformanceMetric) {
        val message = when (metric) {
            is PerformanceMetric.Network -> {
                "NETWORK: [${metric.statusCode}] ${metric.method} ${metric.url} (${metric.responseTime}ms)"
            }
            is PerformanceMetric.Resource -> {
                "RESOURCE: ${metric.type} = ${metric.value}${metric.unit}"
            }
            is PerformanceMetric.ScreenLoad -> {
                "SCREEN: ${metric.screenName} loaded in ${metric.loadTime}ms"
            }
            is PerformanceMetric.Trace -> {
                "TRACE: ${metric.name} duration: ${metric.durationMs}ms"
            }
        }
        
        val type = when (metric) {
            is PerformanceMetric.Network -> MessageType.NETWORK
            is PerformanceMetric.Resource -> MessageType.RESOURCE
            is PerformanceMetric.Trace -> MessageType.TRACE
            else -> MessageType.SCREEN
        }

        _metricsFlow.emit(ConsoleMessage(type, message))
    }

    override suspend fun startTrace(traceKey: String, properties: Map<String, Any>?) {
        _metricsFlow.emit(ConsoleMessage(MessageType.TRACE, "NATIVE START: $traceKey"))
    }

    override suspend fun stopTrace(traceKey: String, properties: Map<String, Any>?) {
        _metricsFlow.emit(ConsoleMessage(MessageType.TRACE, "NATIVE STOP: $traceKey"))
    }

    override suspend fun cancelTrace(traceKey: String) {
        _metricsFlow.emit(ConsoleMessage(MessageType.TRACE, "NATIVE CANCEL: $traceKey"))
    }
}

/**
 * Represents a formatted message to be displayed in the UI console.
 */
data class ConsoleMessage(
    val type: MessageType,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Types of messages for color coding in the console.
 */
enum class MessageType {
    EVENT, NETWORK, RESOURCE, TRACE, SCREEN
}
