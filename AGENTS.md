# Monitorkit: General Context

"Data-driven decisions, not assumptions."

## Overview
Monitorkit is a specialized library for real-time performance monitoring and system health. It enables developers to track custom events, measure system resource usage (CPU/Memory), monitor network performance (including status codes and response times), and diagnose screen loading times to ensure a smooth, optimized user experience.

## Architecture
The library follows **Clean Architecture** principles to ensure maintainability, scalability, and testability.

### Layers and Patterns
- **Domain Layer**: Contains the core business logic using the **Repository** and **UseCase** patterns. It uses **Sealed Classes** (`PerformanceMetric`) to define extensible and type-safe performance data.
- **Presentation Layer (`sdk` folder)**: Houses the `MonitorkitManager`, a Hilt-injectable singleton that acts as the main entry point for the library.
- **Data Layer**: Defines the `MonitorProvider` interface and uses a `MonitorDataSource` with a `CopyOnWriteArrayList` for efficient, thread-safe provider management.

## Core Features
- **MonitorkitManager**: A centralized manager that coordinates event tracking and metric collection. It supports a fluent API for adding and removing providers dynamically.
- **Dynamic Provider Management**: Supports multiple `MonitorProvider` implementations simultaneously. Providers can be added or removed at runtime using unique keys.
- **Rich Metrics**:
    - **Resource**: System CPU and Memory usage.
    - **Network**: HTTP method, URL, status codes, and response latency.
    - **ScreenLoad**: Precise measurement of screen/activity loading times.
- **Library Agnostic**: The core library has zero third-party dependencies. Integrations (Firebase, Sentry, etc.) are implemented in the consumer application.

## Standards
- **Documentation**: All classes and public APIs are fully documented using **KDocs**.
- **Testing**: Robust unit testing with 100% logic coverage using:
    - **JUnit**
    - **MockK**
    - **Kotlin Coroutines Test**
    - Provided via the `libs.plugins.pluginkit.android.testing` plugin.
- **Performance**: High-frequency operations are optimized for low overhead and thread safety.
