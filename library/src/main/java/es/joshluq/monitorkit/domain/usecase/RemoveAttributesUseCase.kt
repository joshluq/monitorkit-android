package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [RemoveAttributesUseCase].
 *
 * @property keys The list of attribute keys to remove.
 * @property providerKey Optional key to target a specific provider.
 */
data class RemoveAttributesInput(
    val keys: List<String>,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for removing multiple global attributes from providers.
 *
 * @property repository The repository where the attributes will be removed.
 */
class RemoveAttributesUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<RemoveAttributesInput, NoneOutput> {

    /**
     * Executes the remove attributes operation.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: RemoveAttributesInput): Flow<NoneOutput> = flow {
        repository.removeAttributes(input.keys, input.providerKey)
        emit(NoneOutput)
    }
}
