package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class SetAttributesInput(
    val attributes: Map<String, String>,
    val providerKey: String? = null
) : UseCaseInput

internal class SetAttributesUseCase(
    private val repository: MonitorRepository
) : UseCase<SetAttributesInput, NoneOutput> {

    override suspend fun invoke(input: SetAttributesInput): Result<NoneOutput> = runCatching {
        repository.setAttributes(input.attributes, input.providerKey)
        NoneOutput
    }
}
