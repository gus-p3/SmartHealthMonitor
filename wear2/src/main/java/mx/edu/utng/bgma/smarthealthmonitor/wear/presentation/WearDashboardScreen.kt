package mx.edu.utng.bgma.smarthealthmonitor.wear.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import mx.edu.utng.bgma.smarthealthmonitor.wear.presentation.components.WearFCCard

@Composable
fun WearDashboardScreen(
    onAlertClick: () -> Unit = {},
    viewModel: WearDashboardViewModel = viewModel()
) {
    val fc by viewModel.fc.collectAsState()
    val pasos by viewModel.pasos.collectAsState()
    val listState = rememberScalingLazyListState()

    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.scrollAway(listState))
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                WearFCCard(
                    fc = fc,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Reto adicional: CompactChip para pasos
            item {
                CompactChip(
                    label = { 
                        Text(if (pasos == 0) "-- pasos" else "$pasos pasos") 
                    },
                    onClick = { /* Acción opcional */ },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Chip(
                    label = { Text("⚠ Alerta") },
                    onClick = onAlertClick,
                    colors = ChipDefaults.primaryChipColors(
                        backgroundColor = MaterialTheme.colors.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
