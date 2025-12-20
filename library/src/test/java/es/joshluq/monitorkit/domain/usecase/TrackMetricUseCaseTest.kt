package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackMetricUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = TrackMetricUseCase(repository)

    @Test
    fun `invoke should call repository trackMetric and emit NoneOutput`() = runTest {
        // Given
        val metric = PerformanceMetric.Resource(ResourceType.CPU, 10.0, "%")
        val input = TrackMetricInput(metric)
        coEvery { repository.trackMetric(any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        coVerify(exactly = 1) { repository.trackMetric(metric, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
