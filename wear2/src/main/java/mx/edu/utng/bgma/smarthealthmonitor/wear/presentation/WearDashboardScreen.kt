package mx.edu.utng.bgma.smarthealthmonitor.wear.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.wear.HealthDataService
import mx.edu.utng.bgma.smarthealthmonitor.wear.presentation.components.WearFCCard
import mx.edu.utng.bgma.smarthealthmonitor.wear.presentation.models.WearDashboardViewModel
import kotlin.random.Random

@Composable
fun WearDashboardScreen(
    onAlertClick: () -> Unit = {},
    navController: NavHostController,
    viewModel: WearDashboardViewModel = viewModel()
) {
    val fc by viewModel.fc.collectAsState()
    val pasos by viewModel.pasos.collectAsState()
    val listState = rememberScalingLazyListState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

            // Botón para enviar estadísticas aleatorias (Test)
            item {
                Chip(
                    label = { Text("📤 Enviar Datos Test") },
                    onClick = {
                        scope.launch {
                            val randomFC = Random.nextInt(60, 120)
                            val randomPasos = Random.nextInt(100, 10000)
                            HealthDataService.enviarFCDirectamente(context, randomFC)
                            HealthDataService.enviarPasosDirectamente(context, randomPasos)
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botón nuevo: Enviar Test MQTT (Por internet)
            item {
                Chip(
                    label = { Text("☁️ Enviar Test MQTT") },
                    onClick = {
                        scope.launch {
                            val randomFC = Random.nextInt(60, 120)
                            val randomPasos = Random.nextInt(100, 10000)
                            // Actualizar localmente la pantalla del reloj
                            SmartHealthRepository.actualizarFC(randomFC)
                            SmartHealthRepository.actualizarPasos(randomPasos)
                            // Publicar por MQTT de forma manual
                            viewModel.publicarManualMqtt(randomFC)
                            // Mostrar mensaje de confirmación en el reloj
                            android.widget.Toast.makeText(context, "Datos enviados", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ChipDefaults.primaryChipColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
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

            item {
                Chip(
                    label = { Text("📋 Historial") },
                    onClick = { navController.navigate(WearScreens.HISTORIAL) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
