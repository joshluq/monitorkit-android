package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [AddProviderUseCase].
 *
 * @property provider The provider to be added to the library.
 */
data class AddProviderInput(
    val provider: MonitorProvider
) : UseCaseInput

/**
 * Use case for adding a new monitoring provider.
 *
 * @property repository The repository where the provider will be registered.
 */
class AddProviderUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<AddProviderInput, NoneOutput> {

    /**
     * Executes the registration of a new provider.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: AddProviderInput): Flow<NoneOutput> = flow {
        repository.addProvider(input.provider)
        emit(NoneOutput)
    }
}
