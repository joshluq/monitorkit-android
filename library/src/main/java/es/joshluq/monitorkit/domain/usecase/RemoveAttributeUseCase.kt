package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class RemoveAttributeInput(
    val key: String,
    val providerKey: String? = null
) : UseCaseInput

internal class RemoveAttributeUseCase(
    private val repository: MonitorRepository
) : UseCase<RemoveAttributeInput, NoneOutput> {

    override fun invoke(input: RemoveAttributeInput): Flow<NoneOutput> = flow {
        repository.removeAttribute(input.key, input.providerKey)
        emit(NoneOutput)
    }
}
