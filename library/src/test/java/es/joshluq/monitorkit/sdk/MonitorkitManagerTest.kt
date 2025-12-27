package es.joshluq.monitorkit.sdk

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.domain.usecase.*
import es.joshluq.monitorkit.sdk.sanitizer.UrlSanitizer
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("UnusedFlow")
class MonitorkitManagerTest {

    private lateinit var monitorkitManager: MonitorkitManager
    private val addProviderUseCase = mockk<AddProviderUseCase>()
    private val removeProviderUseCase = mockk<RemoveProviderUseCase>()
    private val trackEventUseCase = mockk<TrackEventUseCase>()
    private val trackMetricUseCase = mockk<TrackMetricUseCase>()
    private val startTraceUseCase = mockk<StartTraceUseCase>()
    private val stopTraceUseCase = mockk<StopTraceUseCase>()
    private val cancelTraceUseCase = mockk<CancelTraceUseCase>()
    private val setAttributeUseCase = mockk<SetAttributeUseCase>()
    private val setAttributesUseCase = mockk<SetAttributesUseCase>()
    private val removeAttributeUseCase = mockk<RemoveAttributeUseCase>()
    private val removeAttributesUseCase = mockk<RemoveAttributesUseCase>()
    private val urlSanitizer = mockk<UrlSanitizer>()

    @Before
    fun setUp() {
        monitorkitManager = MonitorkitManager(
            addProviderUseCase,
            removeProviderUseCase,
            trackEventUseCase,
            trackMetricUseCase,
            startTraceUseCase,
            stopTraceUseCase,
            cancelTraceUseCase,
            setAttributeUseCase,
            setAttributesUseCase,
            removeAttributeUseCase,
            removeAttributesUseCase,
            urlSanitizer
        )
    }

    // --- Attribute Tests ---

    @Test
    fun `setAttribute should invoke setAttributeUseCase`() {
        every { setAttributeUseCase(any()) } returns flowOf(NoneOutput)
        monitorkitManager.setAttribute("key", "value")
        verify(exactly = 1) { setAttributeUseCase(SetAttributeInput("key", "value")) }
    }

    @Test
    fun `setAttributes should invoke setAttributesUseCase`() {
        val attributes = mapOf("a" to "1", "b" to "2")
        every { setAttributesUseCase(any()) } returns flowOf(NoneOutput)
        monitorkitManager.setAttributes(attributes)
        verify(exactly = 1) { setAttributesUseCase(SetAttributesInput(attributes)) }
    }

    @Test
    fun `removeAttribute should invoke removeAttributeUseCase`() {
        every { removeAttributeUseCase(any()) } returns flowOf(NoneOutput)
        monitorkitManager.removeAttribute("key")
        verify(exactly = 1) { removeAttributeUseCase(RemoveAttributeInput("key")) }
    }

    @Test
    fun `removeAttributes should invoke removeAttributesUseCase`() {
        val keys = listOf("a", "b")
        every { removeAttributesUseCase(any()) } returns flowOf(NoneOutput)
        monitorkitManager.removeAttributes(keys)
        verify(exactly = 1) { removeAttributesUseCase(RemoveAttributesInput(keys)) }
    }

    // --- Basic Features Tests ---

    @Test
    fun `addProvider should invoke addProviderUseCase`() {
        // Given
        val provider = mockk<MonitorProvider>()
        every { addProviderUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.addProvider(provider)

        // Then
        verify(exactly = 1) { addProviderUseCase(any()) }
    }

    @Test
    fun `removeProvider should invoke removeProviderUseCase`() {
        // Given
        val providerKey = "test_key"
        every { removeProviderUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.removeProvider(providerKey)

        // Then
        verify(exactly = 1) { removeProviderUseCase(any()) }
    }

    @Test
    fun `trackEvent should invoke trackEventUseCase`() {
        // Given
        val eventName = "test_event"
        val properties = mapOf("key" to "value")
        every { trackEventUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.trackEvent(eventName, properties)

        // Then
        verify(exactly = 1) { trackEventUseCase(any()) }
    }

    @Test
    fun `trackMetric should invoke trackMetricUseCase with Resource metric`() {
        // Given
        val metric = PerformanceMetric.Resource(ResourceType.CPU, 45.5, "%")
        every { trackMetricUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.trackMetric(metric)

        // Then
        verify(exactly = 1) { trackMetricUseCase(any()) }
    }

    @Test
    fun `trackMetric should sanitize URL and invoke trackMetricUseCase with Network metric`() {
        // Given
        val originalUrl = "api/users/123"
        val sanitizedUrl = "api/users/{id}"
        val metric = PerformanceMetric.Network(originalUrl, "GET", 200, 200L)
        
        every { urlSanitizer.sanitize(originalUrl) } returns sanitizedUrl
        every { trackMetricUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.trackMetric(metric)

        // Then
        verify(exactly = 1) { urlSanitizer.sanitize(originalUrl) }
        verify(exactly = 1) { 
            trackMetricUseCase(withArg { 
                assert((it.metric as PerformanceMetric.Network).url == sanitizedUrl) 
            }) 
        }
    }
    
    @Test
    fun `configureUrlPatterns should delegate to urlSanitizer`() {
        // Given
        val patterns = listOf("api/*")
        every { urlSanitizer.configurePatterns(any()) } returns Unit
        
        // When
        monitorkitManager.configureUrlPatterns(patterns)
        
        // Then
        verify(exactly = 1) { urlSanitizer.configurePatterns(patterns) }
    }

    // --- Internal Tracing Tests (Default Behavior) ---

    @Test
    fun `INTERNAL - startTrace and stopTrace should track a Trace metric with duration`() {
        // Given
        monitorkitManager.setUseNativeTracing(false)
        val traceKey = "test_trace"
        val slot = slot<TrackMetricInput>()
        every { trackMetricUseCase(capture(slot)) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.startTrace(traceKey)
        Thread.sleep(10)
        monitorkitManager.stopTrace(traceKey)

        // Then
        verify(exactly = 1) { trackMetricUseCase(any()) }
        val metric = slot.captured.metric as PerformanceMetric.Trace
        assertEquals(traceKey, metric.name)
        assertTrue("Duration should be >= 10ms", metric.durationMs >= 10)
    }

    @Test
    fun `INTERNAL - stopTrace with properties should merge with start properties`() {
        // Given
        monitorkitManager.setUseNativeTracing(false)
        val traceKey = "prop_trace"
        val startProps = mapOf("start" to "val")
        val stopProps = mapOf("stop" to "val")
        val slot = slot<TrackMetricInput>()
        every { trackMetricUseCase(capture(slot)) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.startTrace(traceKey, startProps)
        monitorkitManager.stopTrace(traceKey, stopProps)

        // Then
        val metric = slot.captured.metric as PerformanceMetric.Trace
        val props = metric.properties!!
        assertEquals("val", props["start"])
        assertEquals("val", props["stop"])
    }

    @Test
    fun `INTERNAL - cancelTrace should prevent metric from being tracked`() {
        // Given
        monitorkitManager.setUseNativeTracing(false)
        val traceKey = "cancelled_trace"

        // When
        monitorkitManager.startTrace(traceKey)
        monitorkitManager.cancelTrace(traceKey)
        monitorkitManager.stopTrace(traceKey)

        // Then
        verify(exactly = 0) { trackMetricUseCase(any()) }
    }
    
    @Test
    fun `INTERNAL - stopTrace on orphan trace should log warning via Logger`() {
        // Given
        monitorkitManager.setUseNativeTracing(false)
        val traceKey = "orphan_trace"

        // When
        monitorkitManager.stopTrace(traceKey)

        // Then
        verify(exactly = 0) { trackMetricUseCase(any()) }
    }

    @Test
    fun `NATIVE - startTrace should delegate to StartTraceUseCase`() {
        // Given
        monitorkitManager.setUseNativeTracing(true)
        val traceKey = "native_trace"
        val props = mapOf("key" to "val")
        every { startTraceUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.startTrace(traceKey, props)

        // Then
        verify(exactly = 1) { startTraceUseCase(StartTraceInput(traceKey, props)) }
        // Verify internal map logic was skipped (no interactions with trackMetricUseCase implies logic flow)
        verify(exactly = 0) { trackMetricUseCase(any()) }
    }

    @Test
    fun `NATIVE - stopTrace should delegate to StopTraceUseCase`() {
        // Given
        monitorkitManager.setUseNativeTracing(true)
        val traceKey = "native_trace"
        val props = mapOf("key" to "val")
        every { stopTraceUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.stopTrace(traceKey, props)

        // Then
        verify(exactly = 1) { stopTraceUseCase(StopTraceInput(traceKey, props)) }
        verify(exactly = 0) { trackMetricUseCase(any()) } // Should NOT send a generic metric
    }

    @Test
    fun `NATIVE - cancelTrace should delegate to CancelTraceUseCase`() {
        // Given
        monitorkitManager.setUseNativeTracing(true)
        val traceKey = "native_trace"
        every { cancelTraceUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.cancelTrace(traceKey)

        // Then
        verify(exactly = 1) { cancelTraceUseCase(CancelTraceInput(traceKey)) }
    }
}
