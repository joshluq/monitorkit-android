package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class SetAttributeInput(
    val key: String,
    val value: String,
    val providerKey: String? = null
) : UseCaseInput

internal class SetAttributeUseCase(
    private val repository: MonitorRepository
) : UseCase<SetAttributeInput, NoneOutput> {

    override fun invoke(input: SetAttributeInput): Flow<NoneOutput> = flow {
        repository.setAttribute(input.key, input.value, input.providerKey)
        emit(NoneOutput)
    }
}
