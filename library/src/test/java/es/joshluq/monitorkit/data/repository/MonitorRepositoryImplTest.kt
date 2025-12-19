package es.joshluq.monitorkit.data.repository

import es.joshluq.monitorkit.data.datasource.MonitorDataSource
import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MetricType
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
        val metric = PerformanceMetric(MetricType.MEMORY, 100.0, "MB")
        coEvery { dataSource.trackMetric(any(), any()) } returns Unit

        // When
        repository.trackMetric(metric)

        // Then
        coVerify(exactly = 1) { dataSource.trackMetric(metric, null) }
    }
}
