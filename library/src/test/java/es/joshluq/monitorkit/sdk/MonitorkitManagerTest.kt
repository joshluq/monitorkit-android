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

class MonitorkitManagerTest {

    private lateinit var monitorkitManager: MonitorkitManager
    private val addProviderUseCase = mockk<AddProviderUseCase>(relaxed = true)
    private val removeProviderUseCase = mockk<RemoveProviderUseCase>(relaxed = true)
    private val trackEventUseCase = mockk<TrackEventUseCase>(relaxed = true)
    private val trackMetricUseCase = mockk<TrackMetricUseCase>(relaxed = true)
    private val startTraceUseCase = mockk<StartTraceUseCase>(relaxed = true)
    private val stopTraceUseCase = mockk<StopTraceUseCase>(relaxed = true)
    private val cancelTraceUseCase = mockk<CancelTraceUseCase>(relaxed = true)
    private val setAttributeUseCase = mockk<SetAttributeUseCase>(relaxed = true)
    private val setAttributesUseCase = mockk<SetAttributesUseCase>(relaxed = true)
    private val removeAttributeUseCase = mockk<RemoveAttributeUseCase>(relaxed = true)
    private val removeAttributesUseCase = mockk<RemoveAttributesUseCase>(relaxed = true)
    private val urlSanitizer = mockk<UrlSanitizer>(relaxed = true)

    private fun createBuilder() = MonitorkitManager.Builder(
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

    @Before
    fun setUp() {
        monitorkitManager = createBuilder().build()
    }

    @Test
    fun `Manager initialized via Builder should configure initial patterns and providers`() {
        // Given
        val provider = mockk<MonitorProvider>()
        val patterns = listOf("api/*", "auth/**")
        every { addProviderUseCase(any()) } returns flowOf(NoneOutput)

        // When
        val manager = createBuilder()
            .addProvider(provider)
            .configureUrlPatterns(patterns)
            .setUseNativeTracing(true)
            .build()

        // Then
        verify(exactly = 1) { urlSanitizer.configurePatterns(patterns) }
        verify(exactly = 1) { addProviderUseCase(any()) }
    }

    @Test
    fun `setAttribute should invoke setAttributeUseCase`() {
        every { setAttributeUseCase(any()) } returns flowOf(NoneOutput)
        monitorkitManager.setAttribute("key", "value")
        verify(exactly = 1) { setAttributeUseCase(any()) }
    }

    @Test
    fun `trackMetric should sanitize URL and invoke trackMetricUseCase with Network metric`() {
        val originalUrl = "api/users/123"
        val sanitizedUrl = "api/users/{id}"
        val metric = PerformanceMetric.Network(originalUrl, "GET", 200, 200L)
        
        every { urlSanitizer.sanitize(originalUrl) } returns sanitizedUrl
        every { trackMetricUseCase(any()) } returns flowOf(NoneOutput)

        monitorkitManager.trackMetric(metric)

        verify(exactly = 1) { urlSanitizer.sanitize(originalUrl) }
        verify(exactly = 1) { 
            trackMetricUseCase(withArg { 
                assert((it.metric as PerformanceMetric.Network).url == sanitizedUrl) 
            }) 
        }
    }

    @Test
    fun `INTERNAL - startTrace and stopTrace should track a Trace metric with duration`() {
        val traceKey = "test_trace"
        val slot = slot<TrackMetricInput>()
        every { trackMetricUseCase(capture(slot)) } returns flowOf(NoneOutput)

        monitorkitManager.startTrace(traceKey)
        Thread.sleep(10)
        monitorkitManager.stopTrace(traceKey)

        verify(exactly = 1) { trackMetricUseCase(any()) }
        val metric = slot.captured.metric as PerformanceMetric.Trace
        assertEquals(traceKey, metric.name)
        assertTrue("Duration should be >= 10ms", metric.durationMs >= 10)
    }

    @Test
    fun `NATIVE - startTrace should delegate to StartTraceUseCase`() {
        val manager = createBuilder().setUseNativeTracing(true).build()
        val traceKey = "native_trace"
        every { startTraceUseCase(any()) } returns flowOf(NoneOutput)

        manager.startTrace(traceKey)

        verify(exactly = 1) { startTraceUseCase(any()) }
    }
}
