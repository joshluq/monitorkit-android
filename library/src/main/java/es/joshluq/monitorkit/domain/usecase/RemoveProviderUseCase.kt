package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class RemoveProviderInput(
    val providerKey: String
) : UseCaseInput

internal class RemoveProviderUseCase(
    private val repository: MonitorRepository
) : UseCase<RemoveProviderInput, NoneOutput> {

    override fun invoke(input: RemoveProviderInput): Flow<NoneOutput> = flow {
        repository.removeProvider(input.providerKey)
        emit(NoneOutput)
    }
}
