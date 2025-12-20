# Monitorkit

**"Data-driven decisions, not assumptions."**

Monitorkit is a powerful, lightweight Android library designed for real-time performance monitoring and system health tracking. It empowers developers to move beyond guesswork by providing precise metrics on resource consumption, network performance, and screen responsiveness.

## 🚀 Key Features

- **Resource Monitoring**: Track CPU and Memory usage.
- **Network Insights**: Measure response times, HTTP status codes, and API call details.
- **Screen Performance**: Monitor loading times for activities and composables.
- **Custom Event Tracking**: Define and monitor business-specific events.
- **Dynamic Provider Management**: Add or remove data consumers (Firebase, Sentry, etc.) at runtime.
- **Agnostic Design**: Integrates seamlessly without forcing third-party dependencies.
- **Hilt Ready**: Full support for Dependency Injection.

## 🏗 Architecture

Monitorkit is built using **Clean Architecture** to ensure long-term maintainability and isolation of business logic.

```mermaid
graph TD
    subgraph "Presentation Layer (SDK)"
        M[MonitorkitManager]
    end

    subgraph "Domain Layer"
        UC[UseCases]
        R[Repository Interface]
        MOD[Sealed Models]
    end

    subgraph "Data Layer"
        RepoImpl[Repository Implementation]
        DS[DataSource]
        P[Provider Interface]
    end

    subgraph "Consumer Application"
        ImplP[Provider Implementation]
        ExtLib[Third-party SDKs]
    end

    M --> UC
    UC --> R
    RepoImpl -- implements --> R
    RepoImpl --> DS
    DS --> P
    ImplP -- implements --> P
    ImplP --> ExtLib
```

## 🛠 Usage Example (from Showcase)

### 1. Implement a Provider
Route library data to your monitoring service by implementing `MonitorProvider`.

```kotlin
class LogMonitorProvider(override val key: String = "LOGCAT") : MonitorProvider {
    override suspend fun trackEvent(event: MonitorEvent) {
        Log.d("Monitor", "Event: ${event.name}")
    }

    override suspend fun trackMetric(metric: PerformanceMetric) {
        when (metric) {
            is PerformanceMetric.Resource -> Log.d("Monitor", "Resource: ${metric.type}")
            is PerformanceMetric.Network -> Log.d("Monitor", "Network: ${metric.url} [${metric.statusCode}]")
            is PerformanceMetric.ScreenLoad -> Log.d("Monitor", "Screen: ${metric.screenName}")
        }
    }
}
```

### 2. Manage Providers
The library is Hilt-ready. You can inject `MonitorkitManager` and manage your providers dynamically.

```kotlin
@Inject lateinit var monitorkitManager: MonitorkitManager

// Add a provider
monitorkitManager.addProvider(LogMonitorProvider())

// Remove a provider when no longer needed
monitorkitManager.removeProvider("LOGCAT")
```

### 3. Track Metrics
Use the manager to record different types of performance data.

```kotlin
// Track Network latency and status
monitorkitManager.trackMetric(
    PerformanceMetric.Network("https://api.example.com", "GET", 200, 150L)
)

// Track Screen load time
monitorkitManager.trackMetric(
    PerformanceMetric.ScreenLoad("HomeActivity", 450L)
)
```

## 📂 Project Structure

- `:monitorkit`: The core library module.
    - `sdk`: Public API (`MonitorkitManager`).
    - `domain`: Business logic, Repository interfaces, and Sealed Metric models.
    - `data`: Repository implementation, DataSource, and Provider abstractions.
- `:showcase`: A sample app demonstrating dynamic provider management and multiple metric types.

## 🧪 Quality Assurance

- **KDocs**: Complete API documentation.
- **Unit Testing**: 100% coverage of logic using **JUnit**, **MockK**, and **Coroutines Test**.
- **Efficiency**: Thread-safe provider management using `CopyOnWriteArrayList`.

---

*Developed with focus on performance and reliability.*
