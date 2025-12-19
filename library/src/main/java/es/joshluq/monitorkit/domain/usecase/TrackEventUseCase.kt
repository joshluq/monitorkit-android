package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [TrackEventUseCase].
 *
 * @property event The event to be tracked.
 * @property providerKey Optional key to target a specific provider.
 */
data class TrackEventInput(
    val event: MonitorEvent,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for tracking custom events.
 *
 * @property repository The repository where the event will be sent.
 */
class TrackEventUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<TrackEventInput, NoneOutput> {

    /**
     * Executes the tracking of a custom event.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: TrackEventInput): Flow<NoneOutput> = flow {
        repository.trackEvent(input.event, input.providerKey)
        emit(NoneOutput)
    }
}
