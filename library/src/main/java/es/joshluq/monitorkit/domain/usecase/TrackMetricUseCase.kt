package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.repository.MonitorRepository

internal data class TrackMetricInput(
    val metric: PerformanceMetric,
    val providerKey: String? = null
) : UseCaseInput

internal class TrackMetricUseCase(
    private val repository: MonitorRepository
) : UseCase<TrackMetricInput, NoneOutput> {

    override suspend fun invoke(input: TrackMetricInput): Result<NoneOutput> = runCatching {
        repository.trackMetric(input.metric, input.providerKey)
        NoneOutput
    }
}
