package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [RemoveAttributeUseCase].
 *
 * @property key The attribute key to remove.
 * @property providerKey Optional key to target a specific provider.
 */
data class RemoveAttributeInput(
    val key: String,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for removing a global attribute from providers.
 *
 * @property repository The repository where the attribute will be removed.
 */
class RemoveAttributeUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<RemoveAttributeInput, NoneOutput> {

    /**
     * Executes the remove attribute operation.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: RemoveAttributeInput): Flow<NoneOutput> = flow {
        repository.removeAttribute(input.key, input.providerKey)
        emit(NoneOutput)
    }
}
