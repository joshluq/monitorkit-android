package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackEventUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = TrackEventUseCase(repository)

    @Test
    fun `invoke should call repository trackEvent and emit NoneOutput`() = runTest {
        // Given
        val event = MonitorEvent("test")
        val input = TrackEventInput(event)
        coEvery { repository.trackEvent(any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        coVerify(exactly = 1) { repository.trackEvent(event, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
