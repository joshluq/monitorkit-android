# Monitorkit: General Context

"Data-driven decisions, not assumptions."

## Overview
Monitorkit is a specialized library for real-time performance monitoring and system health. It enables developers to track custom events, measure system resource usage (CPU, Memory), and diagnose performance bottlenecks to ensure a smooth, optimized user experience.

## Architecture
The library follows **Clean Architecture** principles to ensure maintainability, scalability, and testability.

### Layers and Patterns
- **Domain Layer**: Contains the core business logic using the **Repository** and **UseCase** patterns.
- **Presentation Layer (`sdk` folder)**: Houses the `MonitorkitManager` which acts as the main entry point for the library.
- **Data Layer**: Defines the `Provider` interface.

## Core Features
- **Manager & Providers**: The `MonitorkitManager` can handle multiple `Provider` implementations, identifying them by a unique key. If only one provider is present, it is used by default.
- **Library Agnostic**: Monitorkit is designed to be agnostic of third-party libraries. Any external integrations or specific implementations reside in the consumer application.
- **Showcase**: The `showcase` module serves as a primary example of how a consumer application implements and uses the library.

## Standards
- **Documentation**: All classes and public APIs are documented using **KDocs**.
- **Testing**: Robust unit testing is implemented using:
    - **JUnit**
    - **MockK**
    - **Kotlin Coroutines Test**
    - These are provided via the `libs.plugins.pluginkit.android.testing` plugin.
