package es.joshluq.monitorkit.sdk

import es.joshluq.monitorkit.data.provider.MonitorProvider
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.domain.usecase.AddProviderUseCase
import es.joshluq.monitorkit.domain.usecase.RemoveProviderUseCase
import es.joshluq.monitorkit.domain.usecase.TrackEventUseCase
import es.joshluq.monitorkit.domain.usecase.TrackMetricUseCase
import es.joshluq.monitorkit.domain.usecase.NoneOutput
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class MonitorkitManagerTest {

    private lateinit var monitorkitManager: MonitorkitManager
    private val addProviderUseCase = mockk<AddProviderUseCase>()
    private val removeProviderUseCase = mockk<RemoveProviderUseCase>()
    private val trackEventUseCase = mockk<TrackEventUseCase>()
    private val trackMetricUseCase = mockk<TrackMetricUseCase>()

    @Before
    fun setUp() {
        monitorkitManager = MonitorkitManager(
            addProviderUseCase,
            removeProviderUseCase,
            trackEventUseCase,
            trackMetricUseCase
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
    fun `trackMetric should invoke trackMetricUseCase with Network metric`() {
        // Given
        val metric = PerformanceMetric.Network("https://api.com", "GET", 200, 200L)
        every { trackMetricUseCase(any()) } returns flowOf(NoneOutput)

        // When
        monitorkitManager.trackMetric(metric)

        // Then
        verify(exactly = 1) { trackMetricUseCase(any()) }
    }
}
