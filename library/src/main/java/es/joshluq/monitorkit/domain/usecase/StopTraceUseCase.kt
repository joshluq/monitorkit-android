package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class StopTraceInput(
    val traceKey: String,
    val properties: Map<String, Any>? = null,
    val providerKey: String? = null
) : UseCaseInput

internal class StopTraceUseCase(
    private val repository: MonitorRepository
) : UseCase<StopTraceInput, NoneOutput> {

    override suspend fun invoke(input: StopTraceInput): Result<NoneOutput> = runCatching {
        repository.stopTrace(input.traceKey, input.properties, input.providerKey)
        NoneOutput
    }
}
