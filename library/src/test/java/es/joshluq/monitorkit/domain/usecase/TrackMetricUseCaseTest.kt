package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TrackMetricUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = TrackMetricUseCase(repository)

    @Test
    fun `invoke should call repository trackMetric with Resource metric and emit NoneOutput`() = runTest {
        // Given
        val metric = PerformanceMetric.Resource(ResourceType.CPU, 10.0, "%")
        val input = TrackMetricInput(metric)
        coEvery { repository.trackMetric(any(), any()) } returns Unit

        // When
        useCase(input)

        // Then
        coVerify(exactly = 1) { repository.trackMetric(metric, null) }
    }

    @Test
    fun `invoke should call repository trackMetric with Trace metric and emit NoneOutput`() = runTest {
        // Given
        val metric = PerformanceMetric.Trace("login_process", 1500L, mapOf("user_type" to "admin"))
        val input = TrackMetricInput(metric)
        coEvery { repository.trackMetric(any(), any()) } returns Unit

        // When
        useCase(input)

        // Then
        coVerify(exactly = 1) { repository.trackMetric(metric, null) }
    }
}
