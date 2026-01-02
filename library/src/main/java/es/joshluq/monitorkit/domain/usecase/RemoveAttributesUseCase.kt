package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class RemoveAttributesInput(
    val keys: List<String>,
    val providerKey: String? = null
) : UseCaseInput

internal class RemoveAttributesUseCase(
    private val repository: MonitorRepository
) : UseCase<RemoveAttributesInput, NoneOutput> {

    override suspend fun invoke(input: RemoveAttributesInput): Result<NoneOutput> = runCatching {
        repository.removeAttributes(input.keys, input.providerKey)
        NoneOutput
    }
}
