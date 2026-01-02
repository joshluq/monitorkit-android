package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class AddProviderInput(
    val provider: MonitorProvider
) : UseCaseInput

internal class AddProviderUseCase(
    private val repository: MonitorRepository
) : UseCase<AddProviderInput, NoneOutput> {

    override suspend fun invoke(input: AddProviderInput): Result<NoneOutput> = runCatching {
        repository.addProvider(input.provider)
        NoneOutput
    }
}
