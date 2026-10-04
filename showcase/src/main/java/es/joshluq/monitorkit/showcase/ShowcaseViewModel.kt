package es.joshluq.monitorkit.showcase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.joshluq.foundationkit.network.NetworkMonitor
import es.joshluq.foundationkit.network.NetworkStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Showcase application.
 * Manages the state of the UI Metrics Console by subscribing to [UiMonitorProvider]
 * and observes device connectivity via [NetworkMonitor].
 *
 * @property uiMonitorProvider The provider that captures metrics and emits them for the UI.
 * @property networkMonitor The monitor that provides reactive network connectivity updates.
 */
@HiltViewModel
class ShowcaseViewModel @Inject constructor(
    private val uiMonitorProvider: UiMonitorProvider,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _consoleMessages = MutableStateFlow<List<ConsoleMessage>>(emptyList())

    /**
     * Observable list of messages to be displayed in the console.
     */
    val consoleMessages: StateFlow<List<ConsoleMessage>> = _consoleMessages.asStateFlow()

    /**
     * Observable reactive network status (Available, Losing, Lost, Unavailable).
     */
    val networkStatus: StateFlow<NetworkStatus> =
        networkMonitor.status.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkStatus.Available,
        )

    /**
     * Observable boolean indicating whether the device has internet access.
     */
    val isOnline: StateFlow<Boolean> =
        networkMonitor.isOnline.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    companion object {
        private const val MAX_CONSOLE_MESSAGES = 150
    }

    init {
        // Subscribe to the provider's flow to update the UI in real-time
        viewModelScope.launch {
            uiMonitorProvider.metricsFlow.collect { message ->
                appendMessage(message)
            }
        }

        // Emit network status transitions into the console for real-time visibility
        viewModelScope.launch {
            networkMonitor.status.collect { status ->
                appendMessage(
                    ConsoleMessage(
                        type = MessageType.NETWORK,
                        text = "NETWORK STATUS: ${status.name}",
                    ),
                )
            }
        }
    }

    private fun appendMessage(message: ConsoleMessage) {
        _consoleMessages.update { current ->
            if (current.size >= MAX_CONSOLE_MESSAGES) {
                current.drop(current.size - MAX_CONSOLE_MESSAGES + 1) + message
            } else {
                current + message
            }
        }
    }

    /**
     * Clears all messages from the console.
     */
    fun clearConsole() {
        _consoleMessages.value = emptyList()
    }
}
