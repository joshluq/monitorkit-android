package es.joshluq.monitorkit.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A UI component that simulates a terminal console to display monitoring metrics in real-time.
 *
 * It uses a [LazyColumn] with autoscroll capability to show messages emitted by the [UiMonitorProvider].
 *
 * @param messages List of [ConsoleMessage] to display.
 * @param onClear Callback to clear the console messages.
 * @param modifier Modifier for the console container.
 */
@Composable
fun MetricConsoleView(
    messages: List<ConsoleMessage>,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    // Autoscroll logic: whenever the messages list changes, scroll to the last item
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFF1E1E1E)) // Dark background for terminal feel
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "METRICS CONSOLE",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onClear) {
                Text("Clear", fontSize = 10.sp)
            }
        }

        HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                items(messages) { message ->
                    ConsoleRow(message, timeFormatter)
                }
            }
        }
    }
}

@Composable
private fun ConsoleRow(message: ConsoleMessage, formatter: SimpleDateFormat) {
    val color = when (message.type) {
        MessageType.NETWORK -> Color(0xFF4CAF50) // Green
        MessageType.RESOURCE -> Color(0xFF00BCD4) // Cyan
        MessageType.TRACE -> Color(0xFFFFEB3B) // Yellow
        MessageType.EVENT -> Color(0xFFE91E63) // Pink
        MessageType.SCREEN -> Color(0xFF9C27B0) // Purple
    }

    val timestamp = formatter.format(Date(message.timestamp))

    Text(
        text = "[$timestamp] ${message.text}",
        color = color,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
