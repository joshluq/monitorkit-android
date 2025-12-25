package es.joshluq.monitorkit.showcase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Showcase application.
 * Manages the state of the UI Metrics Console by subscribing to [UiMonitorProvider].
 *
 * @property uiMonitorProvider The provider that captures metrics and emits them for the UI.
 */
@HiltViewModel
class ShowcaseViewModel @Inject constructor(
    private val uiMonitorProvider: UiMonitorProvider
) : ViewModel() {

    private val _consoleMessages = MutableStateFlow<List<ConsoleMessage>>(emptyList())
    
    /**
     * Observable list of messages to be displayed in the console.
     */
    val consoleMessages: StateFlow<List<ConsoleMessage>> = _consoleMessages.asStateFlow()

    init {
        // Subscribe to the provider's flow to update the UI in real-time
        viewModelScope.launch {
            uiMonitorProvider.metricsFlow.collect { message ->
                _consoleMessages.update { current -> current + message }
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
