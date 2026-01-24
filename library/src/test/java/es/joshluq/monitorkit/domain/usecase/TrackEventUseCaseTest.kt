package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
        useCase(input)

        // Then
        coVerify(exactly = 1) { repository.trackEvent(event, null) }
    }
}
