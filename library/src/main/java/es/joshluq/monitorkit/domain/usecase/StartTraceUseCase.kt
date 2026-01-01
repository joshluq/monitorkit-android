package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class StartTraceInput(
    val traceKey: String,
    val properties: Map<String, Any>? = null,
    val providerKey: String? = null
) : UseCaseInput

internal class StartTraceUseCase(
    private val repository: MonitorRepository
) : UseCase<StartTraceInput, NoneOutput> {

    override fun invoke(input: StartTraceInput): Flow<NoneOutput> = flow {
        repository.startTrace(input.traceKey, input.properties, input.providerKey)
        emit(NoneOutput)
    }
}
