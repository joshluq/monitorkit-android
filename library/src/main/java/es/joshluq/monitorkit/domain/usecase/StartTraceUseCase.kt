package es.joshluq.monitorkit.domain.usecase

import es.joshluq.foundationkit.usecase.NoneOutput
import es.joshluq.foundationkit.usecase.UseCase
import es.joshluq.foundationkit.usecase.UseCaseInput
import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class StartTraceInput(
    val traceKey: String,
    val properties: Map<String, Any>? = null,
    val providerKey: String? = null
) : UseCaseInput

internal class StartTraceUseCase(
    private val repository: MonitorRepository
) : UseCase<StartTraceInput, NoneOutput> {

    override suspend fun invoke(input: StartTraceInput): Result<NoneOutput> = runCatching {
        repository.startTrace(input.traceKey, input.properties, input.providerKey)
        NoneOutput
    }
}
