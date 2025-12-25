package es.joshluq.monitorkit.showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.sdk.MonitorkitManager
import es.joshluq.monitorkit.showcase.ui.theme.ShowcaseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var monitorkitManager: MonitorkitManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShowcaseTheme {
                val showcaseViewModel: ShowcaseViewModel = viewModel()
                val consoleMessages by showcaseViewModel.consoleMessages.collectAsState()
                
                var isProviderActive by remember { mutableStateOf(true) }
                var isNativeTracing by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {

                        MonitorControls(
                            modifier = Modifier.weight(2f),
                            isProviderActive = isProviderActive,
                            isNativeTracing = isNativeTracing,
                            onTrackEvent = {
                                monitorkitManager.trackEvent("button_clicked", mapOf("screen" to "main"))
                            },
                            onTrackResource = {
                                monitorkitManager.trackMetric(
                                    PerformanceMetric.Resource(ResourceType.CPU, 25.0, "%")
                                )
                            },
                            onTrackNetworkPattern = {
                                monitorkitManager.trackMetric(
                                    PerformanceMetric.Network("api/users/88552/profile", "GET", 200, 150L)
                                )
                            },
                            onTrackNetworkFallback = {
                                monitorkitManager.trackMetric(
                                    PerformanceMetric.Network("api/orders/999/details/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", "POST", 201, 320L)
                                )
                            },
                            onTrackScreen = {
                                monitorkitManager.trackMetric(
                                    PerformanceMetric.ScreenLoad("MainDashboard", 450L)
                                )
                            },
                            onSimulateTrace = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    monitorkitManager.startTrace("image_process", mapOf("size" to "5MB"))
                                    delay(1500)
                                    monitorkitManager.stopTrace("image_process", mapOf("status" to "success"))
                                }
                            },
                            onToggleProvider = {
                                if (isProviderActive) {
                                    monitorkitManager.removeProvider("LOGCAT")
                                } else {
                                    monitorkitManager.addProvider(LogMonitorProvider())
                                }
                                isProviderActive = !isProviderActive
                            },
                            onToggleNativeTracing = {
                                isNativeTracing = !isNativeTracing
                                monitorkitManager.setUseNativeTracing(isNativeTracing)
                            }
                        )

                        MetricConsoleView(
                            messages = consoleMessages,
                            onClear = { showcaseViewModel.clearConsole() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonitorControls(
    modifier: Modifier = Modifier,
    isProviderActive: Boolean,
    isNativeTracing: Boolean,
    onTrackEvent: () -> Unit,
    onTrackResource: () -> Unit,
    onTrackNetworkPattern: () -> Unit,
    onTrackNetworkFallback: () -> Unit,
    onTrackScreen: () -> Unit,
    onSimulateTrace: () -> Unit,
    onToggleProvider: () -> Unit,
    onToggleNativeTracing: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Monitorkit Showcase Controls")
        
        Button(onClick = onTrackEvent, modifier = Modifier.padding(4.dp)) {
            Text(text = "Track Custom Event")
        }
        
        Button(onClick = onTrackResource, modifier = Modifier.padding(4.dp)) {
            Text(text = "Track Resource (CPU)")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Network Sanitization")

        Button(onClick = onTrackNetworkPattern, modifier = Modifier.padding(4.dp)) {
            Text(text = "Pattern Match")
        }

        Button(onClick = onTrackNetworkFallback, modifier = Modifier.padding(4.dp)) {
            Text(text = "Generic Fallback")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Tracing Feature")

        Button(onClick = onSimulateTrace, modifier = Modifier.padding(4.dp)) {
            Text(text = "Simulate Trace (1.5s)")
        }

        Button(
            onClick = onToggleNativeTracing,
            modifier = Modifier.padding(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isNativeTracing) Color(0xFF673AB7) else Color.Gray)
        ) {
            Text(text = "Mode: ${if (isNativeTracing) "Native (Provider)" else "Internal (SDK)"}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onToggleProvider, 
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if(isProviderActive) Color.Red else Color.Green)
        ) {
            Text(text = if (isProviderActive) "Remove Log Provider" else "Add Log Provider")
        }
    }
}
