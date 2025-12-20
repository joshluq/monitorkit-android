package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class AddProviderUseCaseTest {

    private val repository = mockk<MonitorRepository>()
    private val useCase = AddProviderUseCase(repository)

    @Test
    fun `invoke should call repository addProvider and emit NoneOutput`() = runTest {
        // Given
        val provider = mockk<MonitorProvider>()
        val input = AddProviderInput(provider)
        every { repository.addProvider(any()) } returns Unit

        // When
        val result = useCase(input).toList()

        // Then
        verify(exactly = 1) { repository.addProvider(provider) }
        assertTrue(result.first() is NoneOutput)
    }
}
