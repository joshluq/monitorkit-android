package es.joshluq.monitorkit.sdk

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.domain.usecase.AddProviderUseCase
import es.joshluq.monitorkit.domain.usecase.RemoveProviderUseCase
import es.joshluq.monitorkit.domain.usecase.TrackEventUseCase
import es.joshluq.monitorkit.domain.usecase.TrackMetricUseCase
import es.joshluq.monitorkit.domain.usecase.NoneOutput
import es.joshluq.monitorkit.sdk.sanitizer.UrlSanitizer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

@Suppress("UnusedFlow")
class MonitorkitManagerTest {

    private lateinit var monitorkitManager: MonitorkitManager
    private val addProviderUseCase = mockk<AddProviderUseCase>()
    private val removeProviderUseCase = mockk<RemoveProviderUseCase>()
    private val trackEventUseCase = mockk<TrackEventUseCase>()
    private val trackMetricUseCase = mockk<TrackMetricUseCase>()
    private val urlSanitizer = mockk<UrlSanitizer>()

    @Before
    fun setUp() {
        monitorkitManager = MonitorkitManager(
            addProviderUseCase,
            removeProviderUseCase,
            trackEventUseCase,
            trackMetricUseCase,
            urlSanitizer
        )
    }

    @Test
    fun `addProvider should invoke addProviderUseCase`() {
        // Given
        val provider = mockk<MonitorProvider>()
        every { addProviderUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.addProvider(provider)

        // Then
        verify(exactly = 1) { addProviderUseCase(any()) }
    }

    @Test
    fun `removeProvider should invoke removeProviderUseCase`() {
        // Given
        val providerKey = "test_key"
        every { removeProviderUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.removeProvider(providerKey)

        // Then
        verify(exactly = 1) { removeProviderUseCase(any()) }
    }

    @Test
    fun `trackEvent should invoke trackEventUseCase`() {
        // Given
        val eventName = "test_event"
        val properties = mapOf("key" to "value")
        every { trackEventUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.trackEvent(eventName, properties)

        // Then
        verify(exactly = 1) { trackEventUseCase(any()) }
    }

    @Test
    fun `trackMetric should invoke trackMetricUseCase with Resource metric`() {
        // Given
        val metric = PerformanceMetric.Resource(ResourceType.CPU, 45.5, "%")
        every { trackMetricUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.trackMetric(metric)

        // Then
        verify(exactly = 1) { trackMetricUseCase(any()) }
    }

    @Test
    fun `trackMetric should sanitize URL and invoke trackMetricUseCase with Network metric`() {
            // Given
            val originalUrl = "api/users/123"
            val sanitizedUrl = "api/users/{id}"
            val metric = PerformanceMetric.Network(originalUrl, "GET", 200, 200L)

            every { urlSanitizer.sanitize(originalUrl) } returns sanitizedUrl
            every { trackMetricUseCase(any()) } returns flowOf(NoneOutput)

            // When
            monitorkitManager.trackMetric(metric)

            // Then
            verify(exactly = 1) { urlSanitizer.sanitize(originalUrl) }
            verify(exactly = 1) {
                trackMetricUseCase(withArg {
                    assert((it.metric as PerformanceMetric.Network).url == sanitizedUrl)
                })
            }
        }
    
    @Test
    fun `configureUrlPatterns should delegate to urlSanitizer`() {
        // Given
        val patterns = listOf("api/*")
        every { urlSanitizer.configurePatterns(any()) } returns Unit
        
        // When
        monitorkitManager.configureUrlPatterns(patterns)
        
        // Then
        verify(exactly = 1) { urlSanitizer.configurePatterns(patterns) }
    }
}
