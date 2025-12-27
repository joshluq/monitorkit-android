package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SetAttributeUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = SetAttributeUseCase(repository)

    @Test
    fun `invoke should call repository setAttribute and emit NoneOutput`() = runTest {
        // Given
        val key = "user_tier"
        val value = "premium"
        val input = SetAttributeInput(key, value)
        coEvery { repository.setAttribute(any(), any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        coVerify(exactly = 1) { repository.setAttribute(key, value, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
