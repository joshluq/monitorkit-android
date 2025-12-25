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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import es.joshluq.monitorkit.domain.model.PerformanceMetric
import es.joshluq.monitorkit.domain.model.ResourceType
import es.joshluq.monitorkit.sdk.MonitorkitManager
import es.joshluq.monitorkit.showcase.ui.theme.ShowcaseTheme
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
                var isProviderActive by remember { mutableStateOf(true) }
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MonitorScreen(
                        modifier = Modifier.padding(innerPadding),
                        isProviderActive = isProviderActive,
                        onTrackEvent = {
                            monitorkitManager.trackEvent("button_clicked", mapOf("screen" to "main"))
                        },
                        onTrackResource = {
                            monitorkitManager.trackMetric(
                                PerformanceMetric.Resource(ResourceType.CPU, 25.0, "%")
                            )
                        },
                        onTrackNetworkPattern = {
                            // Should match pattern: api/users/*/profile
                            monitorkitManager.trackMetric(
                                PerformanceMetric.Network("api/users/88552/profile", "GET", 200, 150L)
                            )
                        },
                        onTrackNetworkFallback = {
                            // Should fallback to: api/orders/{id}/details/{uuid}
                            monitorkitManager.trackMetric(
                                PerformanceMetric.Network("api/orders/999/details/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", "POST", 201, 320L)
                            )
                        },
                        onTrackScreen = {
                            monitorkitManager.trackMetric(
                                PerformanceMetric.ScreenLoad("MainDashboard", 450L)
                            )
                        },
                        onToggleProvider = {
                            if (isProviderActive) {
                                monitorkitManager.removeProvider("LOGCAT")
                            } else {
                                monitorkitManager.addProvider(LogMonitorProvider())
                            }
                            isProviderActive = !isProviderActive
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
    isProviderActive: Boolean,
    onTrackEvent: () -> Unit,
    onTrackResource: () -> Unit,
    onTrackNetworkPattern: () -> Unit,
    onTrackNetworkFallback: () -> Unit,
    onTrackScreen: () -> Unit,
    onToggleProvider: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Monitorkit Showcase", modifier = Modifier.padding(bottom = 16.dp))
        
        Button(onClick = onTrackEvent, modifier = Modifier.padding(4.dp)) {
            Text(text = "Track Custom Event")
        }
        
        Button(onClick = onTrackResource, modifier = Modifier.padding(4.dp)) {
            Text(text = "Track Resource (CPU)")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Network Sanitization Tests")

        Button(onClick = onTrackNetworkPattern, modifier = Modifier.padding(4.dp)) {
            Text(text = "Network: Pattern Match (User Profile)")
        }

        Button(onClick = onTrackNetworkFallback, modifier = Modifier.padding(4.dp)) {
            Text(text = "Network: Generic Fallback (ID/UUID)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onTrackScreen, modifier = Modifier.padding(4.dp)) {
            Text(text = "Track Screen Load")
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
