package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class RemoveAttributesInput(
    val keys: List<String>,
    val providerKey: String? = null
) : UseCaseInput

internal class RemoveAttributesUseCase(
    private val repository: MonitorRepository
) : UseCase<RemoveAttributesInput, NoneOutput> {

    override fun invoke(input: RemoveAttributesInput): Flow<NoneOutput> = flow {
        repository.removeAttributes(input.keys, input.providerKey)
        emit(NoneOutput)
    }
}
