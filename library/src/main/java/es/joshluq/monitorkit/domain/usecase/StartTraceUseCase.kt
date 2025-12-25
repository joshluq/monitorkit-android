package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [StartTraceUseCase].
 *
 * @property traceKey Unique identifier for the trace.
 * @property properties Optional initial properties for the trace.
 * @property providerKey Optional key to target a specific provider.
 */
data class StartTraceInput(
    val traceKey: String,
    val properties: Map<String, Any>? = null,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for starting a native custom trace.
 * Delegates the start operation directly to the providers.
 *
 * @property repository The repository to dispatch the operation.
 */
class StartTraceUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<StartTraceInput, NoneOutput> {

    /**
     * Executes the start trace operation.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: StartTraceInput): Flow<NoneOutput> = flow {
        repository.startTrace(input.traceKey, input.properties, input.providerKey)
        emit(NoneOutput)
    }
}
