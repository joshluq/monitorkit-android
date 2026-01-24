package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RemoveProviderUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = RemoveProviderUseCase(repository)

    @Test
    fun `invoke should call repository removeProvider and emit NoneOutput`() = runTest {
        // Given
        val providerKey = "test_key"
        val input = RemoveProviderInput(providerKey)
        every { repository.removeProvider(any()) } returns Unit

        // When
        useCase(input)

        // Then
        verify(exactly = 1) { repository.removeProvider(providerKey) }
    }
}
