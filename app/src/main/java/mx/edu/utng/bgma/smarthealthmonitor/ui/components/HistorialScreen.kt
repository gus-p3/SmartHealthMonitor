package mx.edu.utng.bgma.smarthealthmonitor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.bgma.smarthealthmonitor.ui.components.FilaHistorial
import mx.edu.utng.bgma.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import mx.edu.utng.bgma.smarthealthmonitor.ui.viewmodel.DashboardViewModel

// Extension to convert LocalDate to epoch milliseconds
fun LocalDate.toEpochMillis(): Long =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val lecturas by viewModel.historial.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
 
    SmartHealthMonitorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Historial de FC") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            SmartHealthRepository.limpiarHistorialAntiguo(
                                LocalDate.now().minusDays(1).toEpochMillis()
                            )
                            snackbarHostState.showSnackbar("Historial limpiado")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Limpiar historial"
                    )
                }
            }
        ) { paddingValues ->
            if (lecturas.isEmpty()) {
                // Estado vacío
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay lecturas aún.\nEspera a que el reloj envíe datos.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Text(
                            text = "${lecturas.size} lecturas registradas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(lecturas, key = { it.id }) { lectura ->
                        FilaHistorial(lectura = lectura)
                    }
                }
            }
        }
    }
}