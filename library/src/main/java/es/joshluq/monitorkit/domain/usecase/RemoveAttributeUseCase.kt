package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class RemoveAttributeInput(
    val key: String,
    val providerKey: String? = null
) : UseCaseInput

internal class RemoveAttributeUseCase(
    private val repository: MonitorRepository
) : UseCase<RemoveAttributeInput, NoneOutput> {

    override suspend fun invoke(input: RemoveAttributeInput): Result<NoneOutput> = runCatching {
        repository.removeAttribute(input.key, input.providerKey)
        NoneOutput
    }
}
