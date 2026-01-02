package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class CancelTraceInput(
    val traceKey: String,
    val providerKey: String? = null
) : UseCaseInput

internal class CancelTraceUseCase(
    private val repository: MonitorRepository
) : UseCase<CancelTraceInput, NoneOutput> {

    override suspend fun invoke(input: CancelTraceInput): Result<NoneOutput> = runCatching {
        repository.cancelTrace(input.traceKey, input.providerKey)
        NoneOutput
    }
}
