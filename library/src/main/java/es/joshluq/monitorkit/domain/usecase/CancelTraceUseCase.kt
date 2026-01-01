package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class CancelTraceInput(
    val traceKey: String,
    val providerKey: String? = null
) : UseCaseInput

internal class CancelTraceUseCase(
    private val repository: MonitorRepository
) : UseCase<CancelTraceInput, NoneOutput> {

    override fun invoke(input: CancelTraceInput): Flow<NoneOutput> = flow {
        repository.cancelTrace(input.traceKey, input.providerKey)
        emit(NoneOutput)
    }
}
