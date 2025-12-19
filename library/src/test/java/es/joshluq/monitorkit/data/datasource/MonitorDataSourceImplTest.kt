package es.joshluq.monitorkit.data.datasource

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MetricType
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
    fun `trackEvent should notify only matching provider when key is specified`() = runTest {
        // Given
        val event = MonitorEvent("test_event")
        dataSource.addProvider(provider1)
        dataSource.addProvider(provider2)
        coEvery { provider1.trackEvent(any()) } returns Unit
        coEvery { provider2.trackEvent(any()) } returns Unit

        // When
        dataSource.trackEvent(event, "key1")

        // Then
        coVerify(exactly = 1) { provider1.trackEvent(event) }
        coVerify(exactly = 0) { provider2.trackEvent(event) }
    }

    @Test
    fun `trackMetric should notify all providers when no key is specified`() = runTest {
        // Given
        val metric = PerformanceMetric(MetricType.CPU, 50.0, "%")
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
}
