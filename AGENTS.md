# Monitorkit: General Context

"Data-driven decisions, not assumptions."

## Overview
Monitorkit is a specialized library for real-time performance monitoring and system health. It enables developers to track custom events, measure system resource usage (CPU/Memory), monitor network performance (including status codes and response times), diagnose screen loading times, and measure custom process durations (traces) to ensure a smooth, optimized user experience.

## Architecture
The library follows **Clean Architecture** principles to ensure maintainability, scalability, and testability.

### Layers and Patterns
- **Domain Layer**: Contains the core business logic using the **Repository** and **UseCase** patterns. It uses **Sealed Classes** (`PerformanceMetric`) to define extensible and type-safe performance data.
- **Presentation Layer (`sdk` folder)**: Houses the `MonitorkitManager`, which uses a **Builder Pattern** for its initialization.
- **Data Layer**: Defines the `MonitorProvider` interface and uses a `MonitorDataSource` with a `CopyOnWriteArrayList` for efficient, thread-safe provider management.
- **Manual Dependency Injection**: The SDK is completely agnostic of third-party DI frameworks (like Hilt or Koin). It uses manual dependency injection via its internal `Builder` to instantiate its internal components.

## Core Features
- **MonitorkitManager**: A centralized manager that coordinates event tracking and metric collection. It is initialized using a declarative and fluent `Builder` API.
- **Dynamic Provider Management**: Supports multiple `MonitorProvider` implementations simultaneously. Providers can be added during initialization via the Builder or at runtime using unique keys.
- **Targeted Monitoring**: Most operations (`trackEvent`, `trackMetric`, `setAttribute`, etc.) accept an optional `providerKey`. If provided, the operation is routed only to that specific provider.
- **Global Attributes**: Persistent key-value pairs that can be set at the provider or global level to enrich all subsequent events and metrics with context (e.g., user IDs, feature flags).
- **Custom Tracing**: 
    - **Internal Mode**: The SDK calculates the duration and reports a `Trace` metric.
    - **Native Mode**: Delegates `start`/`stop` calls directly to providers (e.g., for Firebase Performance Trace objects).
- **Rich Metrics**:
    - **Resource**: System CPU and Memory usage.
    - **Network**: HTTP method, URL, status codes, and response latency. Includes automatic **URL Sanitization**.
    - **ScreenLoad**: Precise measurement of screen/activity loading times.
    - **Trace**: Custom process durations with start/stop times.
- **URL Sanitization**: Built-in protection for sensitive data in URLs.
    - **Allowlist Patterns**: Matches specific paths using wildcards (`*` for segments, `**` for suffixes).
    - **Generic Fallback**: Automatically masks UUIDs and numeric IDs with `*` if no pattern matches.
- **Library Agnostic**: The core library has zero third-party dependencies. Integrations (Firebase, Sentry, etc.) are implemented in the consumer application.

## Standards
- **Documentation**: All classes and public APIs are fully documented using **KDocs**.
- **Testing**: Robust unit testing with 100% logic coverage using:
    - **JUnit**
    - **MockK**
    - **Kotlin Coroutines Test**
- **Performance**: High-frequency operations are optimized for low overhead and thread safety.
- **Encapsulation**: Internal components (UseCases, Repositories, Sanitizers) are marked as `internal` to prevent direct access from consumer applications.
