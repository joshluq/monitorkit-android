---
name: monitorkit-architecture-guard
description: Enforce Clean Architecture, internal encapsulation, zero external dependencies in :library, and KDocs standards for Monitorkit.
---

# Monitorkit Architecture & Design Guard

Use this skill whenever you create, refactor, or review components, metrics, or APIs in the Monitorkit project.

## Core Principles

1. **Zero Third-Party Dependencies in `:library`**:
   - The core SDK (`:library`) MUST remain completely library-agnostic.
   - Do NOT add dependencies on Firebase, Sentry, Datadog, Hilt, Koin, etc., inside `library/build.gradle.kts`.
   - Consumer integrations belong exclusively in client apps or the `:showcase` module.

2. **Clean Architecture & Layer Separation**:
   - **Domain Layer (`domain/`)**:
     - `domain/model/`: Sealed classes and data structures (e.g. `PerformanceMetric`).
     - `domain/repository/`: Repository interfaces (e.g. `MonitorRepository`).
     - `domain/usecase/`: Single-responsibility use cases extending FoundationKit base classes or callable as functions.
   - **Data Layer (`data/`)**:
     - `data/datasource/`: `MonitorDataSource` using thread-safe structures (`CopyOnWriteArrayList`).
     - `data/provider/`: `MonitorProvider` contract.
     - `data/repository/`: Implementations of repository interfaces (`MonitorRepositoryImpl`).
   - **Presentation / SDK Layer (`sdk/`)**:
     - Public entry point: `MonitorKitManager` using the Fluent Builder Pattern.
     - Sub-components such as `UrlSanitizer`.

3. **Strict Encapsulation (`internal`)**:
   - All UseCases, Repository implementations, DataSources, and Sanitizers MUST be marked `internal`.
   - Only expose the public API surface: `MonitorKitManager`, `MonitorkitManager.Builder`, `MonitorProvider`, public models (`PerformanceMetric`, `ResourceType`), and explicit public contracts.

4. **KDocs Standards**:
   - Every public class, interface, method, and parameter MUST have meaningful KDocs explaining what it does, arguments (`@param`), and return values (`@return`).

5. **Thread Safety & Performance**:
   - Monitor high-frequency tracing and metric calls.
   - Ensure thread-safe state mutations (e.g., `CopyOnWriteArrayList`, concurrent maps, or atomic references).

## Checklist for New Features / Metrics

- [ ] Does `PerformanceMetric` remain a sealed hierarchy?
- [ ] Are new UseCases marked `internal`?
- [ ] Are new Repository implementations marked `internal`?
- [ ] Is DI handled manually via `MonitorkitManager.Builder` without third-party frameworks?
- [ ] Are all public functions documented with KDocs?
- [ ] Is `:library` free of vendor SDK imports?
