package es.joshluq.monitorkit.showcase

import es.joshluq.foundationkit.network.NetworkMonitor
import es.joshluq.foundationkit.network.NetworkStatus
import es.joshluq.foundationkit.testing.coroutines.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShowcaseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val uiMonitorProvider = mockk<UiMonitorProvider>(relaxed = true)
    private val networkMonitor = mockk<NetworkMonitor>(relaxed = true)
    private val metricsFlow = MutableSharedFlow<ConsoleMessage>(extraBufferCapacity = 10)

    private fun createViewModel(): ShowcaseViewModel {
        every { uiMonitorProvider.metricsFlow } returns metricsFlow
        every { networkMonitor.status } returns flowOf(NetworkStatus.Available)
        every { networkMonitor.isOnline } returns flowOf(true)
        return ShowcaseViewModel(uiMonitorProvider, networkMonitor)
    }

    @Test
    fun `initial state collects network status and starts with empty console or status message`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(NetworkStatus.Available, viewModel.networkStatus.value)
            assertTrue(viewModel.isOnline.value)
        }

    @Test
    fun `when provider emits metric it is added to consoleMessages`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()
            val message = ConsoleMessage(MessageType.NETWORK, "test network")

            metricsFlow.emit(message)
            advanceUntilIdle()

            assertTrue(viewModel.consoleMessages.value.any { it.text == "test network" })
        }

    @Test
    fun `clearConsole empties consoleMessages`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()
            val message = ConsoleMessage(MessageType.EVENT, "event")
            metricsFlow.emit(message)
            advanceUntilIdle()

            viewModel.clearConsole()

            assertTrue(viewModel.consoleMessages.value.isEmpty())
        }
}
