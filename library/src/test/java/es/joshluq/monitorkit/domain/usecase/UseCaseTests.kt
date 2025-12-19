package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.MetricType
import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCaseTests {

    private val repository = mockk<MonitorRepository>()

    @Test
    fun `TrackEventUseCase should call repository and emit NoneOutput`() = runTest {
        // Given
        val useCase = TrackEventUseCase(repository)
        val event = MonitorEvent("test")
        val input = TrackEventInput(event)
        coEvery { repository.trackEvent(any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        coVerify(exactly = 1) { repository.trackEvent(event, null) }
        assertTrue(result.first() is NoneOutput)
    }

    @Test
    fun `TrackMetricUseCase should call repository and emit NoneOutput`() = runTest {
        // Given
        val useCase = TrackMetricUseCase(repository)
        val metric = PerformanceMetric(MetricType.CPU, 10.0, "%")
        val input = TrackMetricInput(metric)
        coEvery { repository.trackMetric(any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        coVerify(exactly = 1) { repository.trackMetric(metric, null) }
        assertTrue(result.first() is NoneOutput)
    }

    @Test
    fun `AddProviderUseCase should call repository and emit NoneOutput`() = runTest {
        // Given
        val useCase = AddProviderUseCase(repository)
        val provider = mockk<MonitorProvider>()
        val input = AddProviderInput(provider)
        every { repository.addProvider(any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        verify(exactly = 1) { repository.addProvider(provider) }
        assertTrue(result.first() is NoneOutput)
    }
}
