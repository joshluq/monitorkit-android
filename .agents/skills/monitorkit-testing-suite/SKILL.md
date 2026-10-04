---
name: monitorkit-testing-suite
description: Best practices, templates, and verification commands for unit testing in Monitorkit with JUnit, MockK, and Coroutines Test aiming for 100% coverage.
---

# Monitorkit Testing Suite

Use this skill whenever you write, update, or verify unit tests in Monitorkit.

## Standards & Tools

- **Framework**: JUnit 4 / JUnit 5 (as configured in project)
- **Mocking**: [MockK](https://mockk.io) (`mockk()`, `coEvery`, `coVerify`, `every`, `verify`)
- **Coroutines**: `kotlinx.coroutines.test` (`runTest`, `StandardTestDispatcher`)
- **Coverage Goal**: 100% logic coverage across UseCases, Repositories, Sanitizers, and DataSources.

## Test Structure Pattern (Given - When - Then)

```kotlin
@Test
fun `useCase should invoke repository and return expected result`() = runTest {
    // Given
    val input = ExampleInput(...)
    coEvery { repository.doAction(any()) } returns Unit

    // When
    useCase(input)

    // Then
    coVerify(exactly = 1) { repository.doAction(...) }
}
```

## Key Test Scenarios to Cover

1. **UseCases**:
   - Happy path with valid input.
   - Routing with and without `providerKey` (broadcast vs targeted).
   - Edge cases: null values, empty maps, invalid identifiers.

2. **UrlSanitizer**:
   - Matching allowlist with wildcard `*` (single segment).
   - Matching allowlist with wildcard `**` (recursive/suffix segments).
   - Generic fallback masking UUIDs (`/users/123e4567-e89b-12d3-a456-426614174000` -> `/users/*`).
   - Generic fallback masking numeric IDs (`/orders/12345` -> `/orders/*`).
   - Query parameter removal/sanitization.

3. **DataSources & Providers**:
   - Dynamic adding and removing of providers by key.
   - Provider isolation when targeted calls are made.

4. **MonitorkitManager**:
   - Builder initialization logic.
   - Internal vs Native trace delegation.

## Verification Commands

Run unit tests and verification from the project root:

```powershell
# Run all unit tests for the library module
.\gradlew.bat :library:testDebugUnitTest

# Run code formatting and linter checks
.\gradlew.bat :library:spotlessCheck
.\gradlew.bat :library:detekt

# Generate Kover coverage report
.\gradlew.bat :library:koverHtmlReport
```
