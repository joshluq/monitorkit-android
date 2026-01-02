package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class SetAttributeInput(
    val key: String,
    val value: String,
    val providerKey: String? = null
) : UseCaseInput

internal class SetAttributeUseCase(
    private val repository: MonitorRepository
) : UseCase<SetAttributeInput, NoneOutput> {

    override suspend fun invoke(input: SetAttributeInput): Result<NoneOutput> = runCatching {
        repository.setAttribute(input.key, input.value, input.providerKey)
        NoneOutput
    }
}
