package es.joshluq.monitorkit.sdk

import es.joshluq.foundationkit.usecase.NoneOutput
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.domain.usecase.*
import es.joshluq.monitorkit.sdk.sanitizer.UrlSanitizer
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MonitorkitManagerTest {

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

    private fun createTestManager(
        useNativeTracing: Boolean = false
    ) = MonitorkitManager(
        addProviderUseCase = addProviderUseCase,
        removeProviderUseCase = removeProviderUseCase,
        trackEventUseCase = trackEventUseCase,
        trackMetricUseCase = trackMetricUseCase,
        startTraceUseCase = startTraceUseCase,
        stopTraceUseCase = stopTraceUseCase,
        cancelTraceUseCase = cancelTraceUseCase,
        setAttributeUseCase = setAttributeUseCase,
        setAttributesUseCase = setAttributesUseCase,
        removeAttributeUseCase = removeAttributeUseCase,
        removeAttributesUseCase = removeAttributesUseCase,
        urlSanitizer = urlSanitizer
    ).apply {
        setUseNativeTracing(useNativeTracing)
    }

    @Test
    fun `addProvider should call addProviderUseCase`() = runTest {
        val manager = createTestManager()
        val provider = mockk<MonitorProvider>()

        manager.addProvider(provider)

        coVerify(timeout = 2000) { addProviderUseCase(AddProviderInput(provider)) }
    }

    @Test
    fun `removeProvider should call removeProviderUseCase`() = runTest {
        val manager = createTestManager()
        val providerKey = "test_provider"

        manager.removeProvider(providerKey)

        coVerify(timeout = 2000) { removeProviderUseCase(RemoveProviderInput(providerKey)) }
    }

    @Test
    fun `setAttribute should call setAttributeUseCase`() = runTest {
        val manager = createTestManager()
        val key = "user_id"
        val value = "123"
        val providerKey = "firebase"

        manager.setAttribute(key, value, providerKey)

        coVerify(timeout = 2000) { setAttributeUseCase(SetAttributeInput(key, value, providerKey)) }
    }

    @Test
    fun `setAttributes should call setAttributesUseCase`() = runTest {
        val manager = createTestManager()
        val attributes = mapOf("key1" to "value1", "key2" to "value2")
        val providerKey = "sentry"

        manager.setAttributes(attributes, providerKey)

        coVerify(timeout = 2000) { setAttributesUseCase(SetAttributesInput(attributes, providerKey)) }
    }

    @Test
    fun `removeAttribute should call removeAttributeUseCase`() = runTest {
        val manager = createTestManager()
        val key = "user_id"
        val providerKey = "firebase"

        manager.removeAttribute(key, providerKey)

        coVerify(timeout = 2000) { removeAttributeUseCase(RemoveAttributeInput(key, providerKey)) }
    }

    @Test
    fun `removeAttributes should call removeAttributesUseCase`() = runTest {
        val manager = createTestManager()
        val keys = listOf("key1", "key2")
        val providerKey = "sentry"

        manager.removeAttributes(keys, providerKey)

        coVerify(timeout = 2000) { removeAttributesUseCase(RemoveAttributesInput(keys, providerKey)) }
    }

    @Test
    fun `trackEvent should call trackEventUseCase`() = runTest {
        val manager = createTestManager()
        val name = "button_click"
        val properties = mapOf("id" to "login")
        val providerKey = "mixpanel"

        manager.trackEvent(name, properties, providerKey)

        coVerify(timeout = 2000) {
            trackEventUseCase(withArg {
                assertEquals(name, it.event.name)
                assertEquals(properties, it.event.properties)
                assertEquals(providerKey, it.providerKey)
            })
        }
    }

    @Test
    fun `trackMetric should sanitize URL for Network metric and call trackMetricUseCase`() = runTest {
        val manager = createTestManager()
        val originalUrl = "https://api.example.com/users/123"
        val sanitizedUrl = "https://api.example.com/users/*"
        val metric = PerformanceMetric.Network(originalUrl, "GET", 200, 150L)
        
        every { urlSanitizer.sanitize(originalUrl) } returns sanitizedUrl

        manager.trackMetric(metric)

        coVerify(timeout = 2000) {
            trackMetricUseCase(withArg {
                val processedMetric = it.metric as PerformanceMetric.Network
                assertEquals(sanitizedUrl, processedMetric.url)
            })
        }
    }

    @Test
    fun `trackMetric should not sanitize URL for non-Network metrics`() = runTest {
        val manager = createTestManager()
        val metric = PerformanceMetric.Resource(ResourceType.CPU, 50.0, "%")

        manager.trackMetric(metric)

        coVerify(timeout = 2000) {
            trackMetricUseCase(withArg {
                assertEquals(metric, it.metric)
            })
        }
        verify(exactly = 0) { urlSanitizer.sanitize(any()) }
    }

    @Test
    fun `INTERNAL - startTrace and stopTrace should track internal duration`() = runTest {
        val manager = createTestManager(useNativeTracing = false)
        val traceKey = "internal_process"
        val initialProperties = mapOf("start" to true)
        val finalProperties = mapOf("end" to true)
        val slot = slot<TrackMetricInput>()

        coEvery { trackMetricUseCase(capture(slot)) } returns Result.success(NoneOutput)

        manager.startTrace(traceKey, initialProperties)
        Thread.sleep(50) // Simulate work using real clock
        manager.stopTrace(traceKey, finalProperties)

        coVerify(timeout = 2000) { trackMetricUseCase(any()) }
        
        val metric = slot.captured.metric as PerformanceMetric.Trace
        assertEquals(traceKey, metric.name)
        assertTrue("Duration should be at least 50ms, was ${metric.durationMs}", metric.durationMs >= 50)
        assertEquals(true, metric.properties?.get("start"))
        assertEquals(true, metric.properties?.get("end"))
    }

    @Test
    fun `INTERNAL - cancelTrace should remove trace from internal map`() = runTest {
        val manager = createTestManager(useNativeTracing = false)
        val traceKey = "cancel_test"

        manager.startTrace(traceKey)
        manager.cancelTrace(traceKey)
        manager.stopTrace(traceKey)

        coVerify(exactly = 0) { trackMetricUseCase(any()) }
    }

    @Test
    fun `NATIVE - startTrace and stopTrace should delegate to use cases`() = runTest {
        val manager = createTestManager(useNativeTracing = true)
        val traceKey = "native_process"
        val properties = mapOf("env" to "prod")

        manager.startTrace(traceKey, properties)
        manager.stopTrace(traceKey, properties)

        coVerify(timeout = 2000) { startTraceUseCase(StartTraceInput(traceKey, properties)) }
        coVerify(timeout = 2000) { stopTraceUseCase(StopTraceInput(traceKey, properties)) }
    }

    @Test
    fun `NATIVE - cancelTrace should delegate to use case`() = runTest {
        val manager = createTestManager(useNativeTracing = true)
        val traceKey = "native_cancel"

        manager.cancelTrace(traceKey)

        coVerify(timeout = 2000) { cancelTraceUseCase(CancelTraceInput(traceKey)) }
    }
}
