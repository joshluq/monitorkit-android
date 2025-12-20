package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [RemoveProviderUseCase].
 *
 * @property providerKey The unique key of the provider to be removed.
 */
data class RemoveProviderInput(
    val providerKey: String
) : UseCaseInput

/**
 * Use case for removing a monitoring provider by its key.
 *
 * @property repository The repository where the provider will be unregistered.
 */
class RemoveProviderUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<RemoveProviderInput, NoneOutput> {

    /**
     * Executes the removal of a provider.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: RemoveProviderInput): Flow<NoneOutput> = flow {
        repository.removeProvider(input.providerKey)
        emit(NoneOutput)
    }
}
