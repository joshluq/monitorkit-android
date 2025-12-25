package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [StopTraceUseCase].
 *
 * @property traceKey Unique identifier for the trace to stop.
 * @property properties Optional final properties for the trace.
 * @property providerKey Optional key to target a specific provider.
 */
data class StopTraceInput(
    val traceKey: String,
    val properties: Map<String, Any>? = null,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for stopping a native custom trace.
 * Delegates the stop operation directly to the providers.
 *
 * @property repository The repository to dispatch the operation.
 */
class StopTraceUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<StopTraceInput, NoneOutput> {

    /**
     * Executes the stop trace operation.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: StopTraceInput): Flow<NoneOutput> = flow {
        repository.stopTrace(input.traceKey, input.properties, input.providerKey)
        emit(NoneOutput)
    }
}
