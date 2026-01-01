package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class TrackEventInput(
    val event: MonitorEvent,
    val providerKey: String? = null
) : UseCaseInput

internal class TrackEventUseCase(
    private val repository: MonitorRepository
) : UseCase<TrackEventInput, NoneOutput> {

    override fun invoke(input: TrackEventInput): Flow<NoneOutput> = flow {
        repository.trackEvent(input.event, input.providerKey)
        emit(NoneOutput)
    }
}
