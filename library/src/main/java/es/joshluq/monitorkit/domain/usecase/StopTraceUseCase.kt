package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class StopTraceInput(
    val traceKey: String,
    val properties: Map<String, Any>? = null,
    val providerKey: String? = null
) : UseCaseInput

internal class StopTraceUseCase(
    private val repository: MonitorRepository
) : UseCase<StopTraceInput, NoneOutput> {

    override fun invoke(input: StopTraceInput): Flow<NoneOutput> = flow {
        repository.stopTrace(input.traceKey, input.properties, input.providerKey)
        emit(NoneOutput)
    }
}
