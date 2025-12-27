package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Input for the [SetAttributesUseCase].
 *
 * @property attributes A map of attribute keys and values.
 * @property providerKey Optional key to target a specific provider.
 */
data class SetAttributesInput(
    val attributes: Map<String, String>,
    val providerKey: String? = null
) : UseCaseInput

/**
 * Use case for setting multiple global attributes on providers.
 *
 * @property repository The repository where the attributes will be set.
 */
class SetAttributesUseCase @Inject constructor(
    private val repository: MonitorRepository
) : UseCase<SetAttributesInput, NoneOutput> {

    /**
     * Executes the set attributes operation.
     *
     * @param input Parameters for the operation.
     * @return A [Flow] emitting [NoneOutput] on completion.
     */
    override fun invoke(input: SetAttributesInput): Flow<NoneOutput> = flow {
        repository.setAttributes(input.attributes, input.providerKey)
        emit(NoneOutput)
    }
}
