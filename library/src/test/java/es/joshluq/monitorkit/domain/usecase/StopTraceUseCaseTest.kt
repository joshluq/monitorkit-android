package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class StopTraceUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = StopTraceUseCase(repository)

    @Test
    fun `invoke should call repository stopTrace and emit NoneOutput`() = runTest {
        // Given
        val key = "trace"
        val props = mapOf("a" to 1)
        val input = StopTraceInput(key, props)
        coEvery { repository.stopTrace(any(), any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        coVerify(exactly = 1) { repository.stopTrace(key, props, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
