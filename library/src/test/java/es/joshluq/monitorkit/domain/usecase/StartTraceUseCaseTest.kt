package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class StartTraceUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = StartTraceUseCase(repository)

    @Test
    fun `invoke should call repository startTrace and emit NoneOutput`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        val input = StartTraceInput(key, props)
        coEvery { repository.startTrace(any(), any(), any()) } returns Unit

        // When
        useCase(input)

        // Then
        coVerify(exactly = 1) { repository.startTrace(key, props, null) }
    }
}
