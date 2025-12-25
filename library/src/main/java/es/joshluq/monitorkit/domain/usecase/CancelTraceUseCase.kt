package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [CancelTraceUseCase].
 *
 * @property traceKey Unique identifier for the trace to cancel.
 * @property providerKey Optional key to target a specific provider.
 */
data class CancelTraceInput(
    val traceKey: String,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for cancelling a native custom trace.
 * Delegates the cancel operation directly to the providers.
 *
 * @property repository The repository to dispatch the operation.
 */
class CancelTraceUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<CancelTraceInput, NoneOutput> {

    /**
     * Executes the cancel trace operation.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: CancelTraceInput): Flow<NoneOutput> = flow {
        repository.cancelTrace(input.traceKey, input.providerKey)
        emit(NoneOutput)
    }
}
