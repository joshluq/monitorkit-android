package es.joshluq.monitorkit.showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MonitorScreen(
                        modifier = Modifier.padding(innerPadding),
                        onTrackEvent = {
                            monitorkitManager.trackEvent("button_clicked", mapOf("screen" to "main"))
                        },
                        onTrackResource = {
                            monitorkitManager.trackMetric(
                                PerformanceMetric.Resource(ResourceType.CPU, 25.0, "%")
                            )
                        },
                        onTrackNetwork = {
                            monitorkitManager.trackMetric(
                                PerformanceMetric.Network("https://api.example.com/data", "GET", 150L)
                            )
                        },
                        onTrackScreen = {
                            monitorkitManager.trackMetric(
                                PerformanceMetric.ScreenLoad("MainDashboard", 450L)
                            )
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
    onTrackEvent: () -> Unit,
    onTrackResource: () -> Unit,
    onTrackNetwork: () -> Unit,
    onTrackScreen: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Monitorkit Showcase", modifier = Modifier.padding(bottom = 16.dp))
        
        Button(onClick = onTrackEvent, modifier = Modifier.padding(8.dp)) {
            Text(text = "Track Custom Event")
        }
        
        Button(onClick = onTrackResource, modifier = Modifier.padding(8.dp)) {
            Text(text = "Track Resource (CPU)")
        }

        Button(onClick = onTrackNetwork, modifier = Modifier.padding(8.dp)) {
            Text(text = "Track Network Call")
        }

        Button(onClick = onTrackScreen, modifier = Modifier.padding(8.dp)) {
            Text(text = "Track Screen Load")
        }
    }
}
