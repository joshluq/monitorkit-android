package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class RemoveProviderInput(
    val providerKey: String
) : UseCaseInput

internal class RemoveProviderUseCase(
    private val repository: MonitorRepository
) : UseCase<RemoveProviderInput, NoneOutput> {

    override suspend fun invoke(input: RemoveProviderInput): Result<NoneOutput> = runCatching {
        repository.removeProvider(input.providerKey)
        NoneOutput
    }
}
