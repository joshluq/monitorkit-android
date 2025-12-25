package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
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
        val result = useCase(input).toList()

        // Then
        coVerify(exactly = 1) { repository.cancelTrace(key, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
