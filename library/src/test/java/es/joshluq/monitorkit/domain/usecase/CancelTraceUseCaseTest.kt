package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CancelTraceUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = CancelTraceUseCase(repository)

    @Test
    fun `invoke should call repository cancelTrace and emit NoneOutput`() = runTest {
        // Given
        val key = "trace"
        val input = CancelTraceInput(key)
        coEvery { repository.cancelTrace(any(), any()) } returns Unit

        // When
        useCase(input)

        // Then
        coVerify(exactly = 1) { repository.cancelTrace(key, null) }
    }
}
