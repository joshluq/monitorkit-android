package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.MonitorEvent
import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class TrackEventInput(
    val event: MonitorEvent,
    val providerKey: String? = null
) : UseCaseInput

internal class TrackEventUseCase(
    private val repository: MonitorRepository
) : UseCase<TrackEventInput, NoneOutput> {

    override suspend fun invoke(input: TrackEventInput): Result<NoneOutput> = runCatching {
        repository.trackEvent(input.event, input.providerKey)
        NoneOutput
    }
}
