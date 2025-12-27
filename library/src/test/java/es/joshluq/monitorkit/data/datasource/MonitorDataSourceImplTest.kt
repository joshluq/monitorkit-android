package es.joshluq.monitorkit.data.datasource

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MonitorDataSourceImplTest {

    private lateinit var dataSource: MonitorDataSourceImpl
    private val provider1 = mockk<MonitorProvider>()
    private val provider2 = mockk<MonitorProvider>()

    @Before
    fun setUp() {
        dataSource = MonitorDataSourceImpl()
        every { provider1.key } returns "key1"
        every { provider2.key } returns "key2"
    }

    @Test
    fun `addProvider should add provider to the list`() = runTest {
        // Given
        val event = MonitorEvent("test_event")
        dataSource.addProvider(provider1)
        coEvery { provider1.trackEvent(any()) } returns Unit

        // When
        dataSource.trackEvent(event)

        // Then
        coVerify(exactly = 1) { provider1.trackEvent(event) }
    }

    @Test
    fun `removeProvider should remove provider from the list`() = runTest {
        // Given
        val event = MonitorEvent("test_event")
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        coEvery { provider1.trackEvent(any()) } returns Unit
        coEvery { provider2.trackEvent(any()) } returns Unit

        // When
        dataSource.removeProvider("key1")
        dataSource.trackEvent(event)

        // Then
        coVerify(exactly = 0) { provider1.trackEvent(event) }
        coVerify(exactly = 1) { provider2.trackEvent(event) }
    }

    @Test
    fun `setAttribute should notify matching providers`() {
        // Given
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        every { provider1.setAttribute(any(), any()) } returns Unit
        every { provider2.setAttribute(any(), any()) } returns Unit

        // When
        dataSource.setAttribute("key", "value", "key1")

        // Then
        verify(exactly = 1) { provider1.setAttribute("key", "value") }
        verify(exactly = 0) { provider2.setAttribute(any(), any()) }
    }

    @Test
    fun `setAttributes should notify all providers when no key is specified`() {
        // Given
        val attrs = mapOf("a" to "1", "b" to "2")
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        every { provider1.setAttributes(any()) } returns Unit
        every { provider2.setAttributes(any()) } returns Unit

        // When
        dataSource.setAttributes(attrs)

        // Then
        verify(exactly = 1) { provider1.setAttributes(attrs) }
        verify(exactly = 1) { provider2.setAttributes(attrs) }
    }

    @Test
    fun `removeAttribute should notify all providers when no key is specified`() {
        // Given
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        every { provider1.removeAttribute(any()) } returns Unit
        every { provider2.removeAttribute(any()) } returns Unit

        // When
        dataSource.removeAttribute("key")

        // Then
        verify(exactly = 1) { provider1.removeAttribute("key") }
        verify(exactly = 1) { provider2.removeAttribute("key") }
    }

    @Test
    fun `removeAttributes should notify all providers when no key is specified`() {
        // Given
        val keys = listOf("a", "b")
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        every { provider1.removeAttributes(any()) } returns Unit
        every { provider2.removeAttributes(any()) } returns Unit

        // When
        dataSource.removeAttributes(keys)

        // Then
        verify(exactly = 1) { provider1.removeAttributes(keys) }
        verify(exactly = 1) { provider2.removeAttributes(keys) }
    }

    @Test
    fun `trackEvent should notify all providers when no key is specified`() = runTest {
        // Given
        val event = MonitorEvent("test_event")
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        coEvery { provider1.trackEvent(any()) } returns Unit
        coEvery { provider2.trackEvent(any()) } returns Unit

        // When
        dataSource.trackEvent(event)

        // Then
        coVerify(exactly = 1) { provider1.trackEvent(event) }
        coVerify(exactly = 1) { provider2.trackEvent(event) }
    }

    @Test
    fun `trackMetric should notify all providers when no key is specified`() = runTest {
        // Given
        val metric = PerformanceMetric.Resource(ResourceType.CPU, 50.0, "%")
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        coEvery { provider1.trackMetric(any()) } returns Unit
        coEvery { provider2.trackMetric(any()) } returns Unit

        // When
        dataSource.trackMetric(metric)

        // Then
        coVerify(exactly = 1) { provider1.trackMetric(metric) }
        coVerify(exactly = 1) { provider2.trackMetric(metric) }
    }

    @Test
    fun `startTrace should notify all providers`() = runTest {
        // Given
        val traceKey = "trace_1"
        val props = mapOf("a" to 1)
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        coEvery { provider1.startTrace(any(), any()) } returns Unit
        coEvery { provider2.startTrace(any(), any()) } returns Unit

        // When
        dataSource.startTrace(traceKey, props, null)

        // Then
        coVerify(exactly = 1) { provider1.startTrace(traceKey, props) }
        coVerify(exactly = 1) { provider2.startTrace(traceKey, props) }
    }

    @Test
    fun `stopTrace should notify all providers`() = runTest {
        // Given
        val traceKey = "trace_1"
        val props = mapOf("b" to 2)
        dataSource.addProvider(provider1)
        coEvery { provider1.stopTrace(any(), any()) } returns Unit

        // When
        dataSource.stopTrace(traceKey, props, null)

        // Then
        coVerify(exactly = 1) { provider1.stopTrace(traceKey, props) }
    }

    @Test
    fun `cancelTrace should notify all providers`() = runTest {
        // Given
        val traceKey = "trace_1"
        dataSource.addProvider(provider1)
        coEvery { provider1.cancelTrace(any()) } returns Unit

        // When
        dataSource.cancelTrace(traceKey, null)

        // Then
        coVerify(exactly = 1) { provider1.cancelTrace(traceKey) }
    }
}
