package es.joshluq.monitorkit.data.repository

import es.joshluq.monitorkit.data.datasource.MonitorDataSource
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
    fun `trackEvent should delegate to dataSource`() = runTest {
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
    fun `trackMetric should delegate to dataSource`() = runTest {
        // Given
        val metric = PerformanceMetric.ScreenLoad("login", 100L)
        coEvery { dataSource.trackMetric(any(), any()) } returns Unit

        // When
        repository.trackMetric(metric)

        // Then
        coVerify(exactly = 1) { dataSource.trackMetric(metric, null) }
    }

    @Test
    fun `startTrace should delegate to dataSource`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        coEvery { dataSource.startTrace(any(), any(), any()) } returns Unit

        // When
        repository.startTrace(key, props, null)

        // Then
        coVerify(exactly = 1) { dataSource.startTrace(key, props, null) }
    }

    @Test
    fun `stopTrace should delegate to dataSource`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        coEvery { dataSource.stopTrace(any(), any(), any()) } returns Unit

        // When
        repository.stopTrace(key, props, null)

        // Then
        coVerify(exactly = 1) { dataSource.stopTrace(key, props, null) }
    }

    @Test
    fun `cancelTrace should delegate to dataSource`() = runTest {
        // Given
        val key = "trace"
        coEvery { dataSource.cancelTrace(any(), any()) } returns Unit

        // When
        repository.cancelTrace(key, null)

        // Then
        coVerify(exactly = 1) { dataSource.cancelTrace(key, null) }
    }
}
