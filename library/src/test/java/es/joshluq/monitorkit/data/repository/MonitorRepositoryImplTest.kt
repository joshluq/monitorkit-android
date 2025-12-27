package es.joshluq.monitorkit.data.repository

import es.joshluq.monitorkit.data.datasource.MonitorDataSource
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MonitorRepositoryImplTest {

    private lateinit var repository: MonitorRepositoryImpl
    private val dataSource = mockk<MonitorDataSource>()

    @Before
    fun setUp() {
        repository = MonitorRepositoryImpl(dataSource)
    }

    @Test
    fun `addProvider should delegate to dataSource`() {
        // Given
        val provider = mockk<MonitorProvider>()
        every { dataSource.addProvider(any()) } returns Unit

        // When
        repository.addProvider(provider)

        // Then
        verify(exactly = 1) { dataSource.addProvider(provider) }
    }

    @Test
    fun `removeProvider should delegate to dataSource`() {
        // Given
        val key = "key"
        every { dataSource.removeProvider(any()) } returns Unit

        // When
        repository.removeProvider(key)

        // Then
        verify(exactly = 1) { dataSource.removeProvider(key) }
    }

    @Test
    fun `trackEvent should delegate to dataSource with key`() = runTest {
        // Given
        val event = MonitorEvent("test_event")
        val key = "some_key"
        coEvery { dataSource.trackEvent(any(), any()) } returns Unit

        // When
        repository.trackEvent(event, key)

        // Then
        coVerify(exactly = 1) { dataSource.trackEvent(event, key) }
    }

    @Test
    fun `trackEvent should delegate to dataSource without key`() = runTest {
        // Given
        val event = MonitorEvent("test_event")
        coEvery { dataSource.trackEvent(any(), any()) } returns Unit

        // When
        repository.trackEvent(event)

        // Then
        coVerify(exactly = 1) { dataSource.trackEvent(event, null) }
    }

    @Test
    fun `trackMetric should delegate to dataSource with key`() = runTest {
        // Given
        val metric = PerformanceMetric.ScreenLoad("login", 100L)
        val key = "some_key"
        coEvery { dataSource.trackMetric(any(), any()) } returns Unit

        // When
        repository.trackMetric(metric, key)

        // Then
        coVerify(exactly = 1) { dataSource.trackMetric(metric, key) }
    }

    @Test
    fun `trackMetric should delegate to dataSource without key`() = runTest {
        // Given
        val metric = PerformanceMetric.ScreenLoad("login", 100L)
        coEvery { dataSource.trackMetric(any(), any()) } returns Unit

        // When
        repository.trackMetric(metric)

        // Then
        coVerify(exactly = 1) { dataSource.trackMetric(metric, null) }
    }

    @Test
    fun `setAttribute should delegate to dataSource with key`() {
        // Given
        every { dataSource.setAttribute(any(), any(), any()) } returns Unit

        // When
        repository.setAttribute("key", "value", "provider")

        // Then
        verify(exactly = 1) { dataSource.setAttribute("key", "value", "provider") }
    }

    @Test
    fun `setAttribute should delegate to dataSource without key`() {
        // Given
        every { dataSource.setAttribute(any(), any(), any()) } returns Unit

        // When
        repository.setAttribute("key", "value")

        // Then
        verify(exactly = 1) { dataSource.setAttribute("key", "value", null) }
    }

    @Test
    fun `setAttributes should delegate to dataSource with key`() {
        // Given
        val attrs = mapOf("a" to "1")
        every { dataSource.setAttributes(any(), any()) } returns Unit

        // When
        repository.setAttributes(attrs, "provider")

        // Then
        verify(exactly = 1) { dataSource.setAttributes(attrs, "provider") }
    }

    @Test
    fun `setAttributes should delegate to dataSource without key`() {
        // Given
        val attrs = mapOf("a" to "1")
        every { dataSource.setAttributes(any(), any()) } returns Unit

        // When
        repository.setAttributes(attrs)

        // Then
        verify(exactly = 1) { dataSource.setAttributes(attrs, null) }
    }

    @Test
    fun `removeAttribute should delegate to dataSource with key`() {
        // Given
        every { dataSource.removeAttribute(any(), any()) } returns Unit

        // When
        repository.removeAttribute("key", "provider")

        // Then
        verify(exactly = 1) { dataSource.removeAttribute("key", "provider") }
    }

    @Test
    fun `removeAttribute should delegate to dataSource without key`() {
        // Given
        every { dataSource.removeAttribute(any(), any()) } returns Unit

        // When
        repository.removeAttribute("key")

        // Then
        verify(exactly = 1) { dataSource.removeAttribute("key", null) }
    }

    @Test
    fun `removeAttributes should delegate to dataSource with key`() {
        // Given
        val keys = listOf("a", "b")
        every { dataSource.removeAttributes(any(), any()) } returns Unit

        // When
        repository.removeAttributes(keys, "provider")

        // Then
        verify(exactly = 1) { dataSource.removeAttributes(keys, "provider") }
    }

    @Test
    fun `removeAttributes should delegate to dataSource without key`() {
        // Given
        val keys = listOf("a", "b")
        every { dataSource.removeAttributes(any(), any()) } returns Unit

        // When
        repository.removeAttributes(keys)

        // Then
        verify(exactly = 1) { dataSource.removeAttributes(keys, null) }
    }

    @Test
    fun `startTrace should delegate to dataSource with key`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        val providerKey = "provider"
        coEvery { dataSource.startTrace(any(), any(), any()) } returns Unit

        // When
        repository.startTrace(key, props, providerKey)

        // Then
        coVerify(exactly = 1) { dataSource.startTrace(key, props, providerKey) }
    }

    @Test
    fun `startTrace should delegate to dataSource without key`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        coEvery { dataSource.startTrace(any(), any(), any()) } returns Unit

        // When
        repository.startTrace(key, props)

        // Then
        coVerify(exactly = 1) { dataSource.startTrace(key, props, null) }
    }

    @Test
    fun `stopTrace should delegate to dataSource with key`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        val providerKey = "provider"
        coEvery { dataSource.stopTrace(any(), any(), any()) } returns Unit

        // When
        repository.stopTrace(key, props, providerKey)

        // Then
        coVerify(exactly = 1) { dataSource.stopTrace(key, props, providerKey) }
    }

    @Test
    fun `stopTrace should delegate to dataSource without key`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        coEvery { dataSource.stopTrace(any(), any(), any()) } returns Unit

        // When
        repository.stopTrace(key, props)

        // Then
        coVerify(exactly = 1) { dataSource.stopTrace(key, props, null) }
    }

    @Test
    fun `cancelTrace should delegate to dataSource with key`() = runTest {
        // Given
        val key = "trace"
        val providerKey = "provider"
        coEvery { dataSource.cancelTrace(any(), any()) } returns Unit

        // When
        repository.cancelTrace(key, providerKey)

        // Then
        coVerify(exactly = 1) { dataSource.cancelTrace(key, providerKey) }
    }

    @Test
    fun `cancelTrace should delegate to dataSource without key`() = runTest {
        // Given
        val key = "trace"
        coEvery { dataSource.cancelTrace(any(), any()) } returns Unit

        // When
        repository.cancelTrace(key)

        // Then
        coVerify(exactly = 1) { dataSource.cancelTrace(key, null) }
    }
}
