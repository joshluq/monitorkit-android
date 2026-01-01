package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class SetAttributesInput(
    val attributes: Map<String, String>,
    val providerKey: String? = null
) : UseCaseInput

internal class SetAttributesUseCase(
    private val repository: MonitorRepository
) : UseCase<SetAttributesInput, NoneOutput> {

    override fun invoke(input: SetAttributesInput): Flow<NoneOutput> = flow {
        repository.setAttributes(input.attributes, input.providerKey)
        emit(NoneOutput)
    }
}
