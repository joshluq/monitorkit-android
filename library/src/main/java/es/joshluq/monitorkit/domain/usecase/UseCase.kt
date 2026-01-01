package es.joshluq.monitorkit.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Base interface for all use case inputs.
 */
internal interface UseCaseInput

/**
 * Base interface for all use case outputs.
 */
internal interface UseCaseOutput

/**
 * Represents a generic Use Case in Clean Architecture.
 */
internal interface UseCase<in I : UseCaseInput, out O : UseCaseOutput> {
    /**
     * Executes the business logic of the use case.
     */
    operator fun invoke(input: I): Flow<O>
}

/**
 * Represents a standard empty output for Use Cases that do not return a specific value.
 */
internal object NoneOutput : UseCaseOutput
