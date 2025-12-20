# Monitorkit: General Context

"Data-driven decisions, not assumptions."

## Overview
Monitorkit is a specialized library for real-time performance monitoring and system health. It enables developers to track custom events, measure system resource usage (Resource), monitor network performance (Network), and diagnose screen loading times (ScreenLoad) to ensure a smooth, optimized user experience.

## Architecture
The library follows **Clean Architecture** principles to ensure maintainability, scalability, and testability.

### Layers and Patterns
- **Domain Layer**: Contains the core business logic using the **Repository** and **UseCase** patterns. Models use **Sealed Classes** for extensible metric definitions.
- **Presentation Layer (`sdk` folder)**: Houses the `MonitorkitManager` which acts as the main entry point for the library.
- **Data Layer**: Defines the `Provider` interface and handles data dispatching through a `DataSource`.

## Core Features
- **Manager & Providers**: The `MonitorkitManager` manages a collection of `MonitorProvider` implementations. Providers can be added dynamically and targeted using unique keys.
- **Extensible Metrics**: Support for various performance metrics including System Resources (CPU/Memory), Network latency, and Screen loading times via a unified `trackMetric` API.
- **Library Agnostic**: Monitorkit is designed to be agnostic of third-party libraries. Any external integrations (Firebase, Sentry, etc.) reside in the consumer application via `Provider` implementations.
- **Showcase**: The `showcase` module serves as a primary example of how a consumer application implements and uses the library with Hilt integration.

## Standards
- **Documentation**: All classes and public APIs are documented using **KDocs**.
- **Testing**: Robust unit testing is implemented using:
    - **JUnit**
    - **MockK**
    - **Kotlin Coroutines Test**
    - These are provided via the `libs.plugins.pluginkit.android.testing` plugin.
- **Concurrency**: High-performance thread-safe state management using `CopyOnWriteArrayList` for provider management.
