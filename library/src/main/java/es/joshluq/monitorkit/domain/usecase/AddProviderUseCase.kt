package es.joshluq.monitorkit.domain.usecase

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class AddProviderInput(
    val provider: MonitorProvider
) : UseCaseInput

internal class AddProviderUseCase(
    private val repository: MonitorRepository
) : UseCase<AddProviderInput, NoneOutput> {

    override fun invoke(input: AddProviderInput): Flow<NoneOutput> = flow {
        repository.addProvider(input.provider)
        emit(NoneOutput)
    }
}
