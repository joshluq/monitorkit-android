package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SetAttributesUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = SetAttributesUseCase(repository)

    @Test
    fun `invoke should call repository setAttributes and emit NoneOutput`() = runTest {
        // Given
        val attributes = mapOf("user_tier" to "premium", "app_version" to "1.0.0")
        val input = SetAttributesInput(attributes)
        every { repository.setAttributes(any(), any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        verify(exactly = 1) { repository.setAttributes(attributes, null) }
        assertTrue(result.first() is NoneOutput)
    }
}
