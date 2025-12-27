package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveAttributesUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = RemoveAttributesUseCase(repository)

    @Test
    fun `invoke should call repository removeAttributes and emit NoneOutput`() = runTest {
        // Given
        val keys = listOf("user_tier", "app_version")
        val input = RemoveAttributesInput(keys)
        every { repository.removeAttributes(any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        verify(exactly = 1) { repository.removeAttributes(keys, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
