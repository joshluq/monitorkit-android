package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class TrackMetricInput(
    val metric: PerformanceMetric,
    val providerKey: String? = null
) : UseCaseInput

internal class TrackMetricUseCase(
    private val repository: MonitorRepository
) : UseCase<TrackMetricInput, NoneOutput> {

    override fun invoke(input: TrackMetricInput): Flow<NoneOutput> = flow {
        repository.trackMetric(input.metric, input.providerKey)
        emit(NoneOutput)
    }
}
