package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [TrackMetricUseCase].
 *
 * @property metric The performance metric to record.
 * @property providerKey Optional key to target a specific provider.
 */
data class TrackMetricInput(
    val metric: PerformanceMetric,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for tracking system performance metrics (CPU, Memory).
 *
 * @property repository The repository where the metrics will be sent.
 */
class TrackMetricUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<TrackMetricInput, NoneOutput> {

    /**
     * Executes the tracking of a performance metric.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: TrackMetricInput): Flow<NoneOutput> = flow {
        repository.trackMetric(input.metric, input.providerKey)
        emit(NoneOutput)
    }
}
