package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveAttributeUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = RemoveAttributeUseCase(repository)

    @Test
    fun `invoke should call repository removeAttribute and emit NoneOutput`() = runTest {
        // Given
        val key = "user_tier"
        val input = RemoveAttributeInput(key)
        every { repository.removeAttribute(any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        verify(exactly = 1) { repository.removeAttribute(key, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
