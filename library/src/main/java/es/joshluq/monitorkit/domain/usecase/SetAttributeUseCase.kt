package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [SetAttributeUseCase].
 *
 * @property key The attribute key.
 * @property value The attribute value.
 * @property providerKey Optional key to target a specific provider.
 */
data class SetAttributeInput(
    val key: String,
    val value: String,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for setting a single global attribute on providers.
 *
 * @property repository The repository where the attribute will be set.
 */
class SetAttributeUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<SetAttributeInput, NoneOutput> {

    /**
     * Executes the set attribute operation.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: SetAttributeInput): Flow<NoneOutput> = flow {
        repository.setAttribute(input.key, input.value, input.providerKey)
        emit(NoneOutput)
    }
}
