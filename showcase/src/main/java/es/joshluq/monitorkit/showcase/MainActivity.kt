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
import es.joshluq.monitorkit.domain.model.MetricType
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
                        onTrackMetric = {
                            monitorkitManager.trackMetric(MetricType.CPU, 25.0, "%")
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
    onTrackMetric: () -> Unit
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
        
        Button(onClick = onTrackMetric, modifier = Modifier.padding(8.dp)) {
            Text(text = "Track CPU Metric")
        }
    }
}
