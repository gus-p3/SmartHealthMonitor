package mx.edu.utng.bgma.smarthealthmonitor.wear.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.wear.HealthDataService
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.SampleDataPoint

class MainActivity : ComponentActivity() {

    private val currentHeartRate = mutableStateOf(0)
    private var isMeasuring = false
    private val measureClient by lazy { HealthServices.getClient(this).measureClient }

    private val measureCallback = object : MeasureCallback {
        override fun onAvailabilityChanged(
            dataType: DeltaDataType<*, *>,
            availability: Availability
        ) {
            Log.d("MainActivity", "Disponibilidad del sensor cambiada: $availability")
        }

        override fun onDataReceived(data: DataPointContainer) {
            val heartRateData = data.getData(DataType.HEART_RATE_BPM)
            heartRateData.forEach { dataPoint ->
                if (dataPoint is SampleDataPoint<Double>) {
                    val bpm = dataPoint.value.toInt()
                    Log.d("MainActivity", "FC del emulador recibida: $bpm BPM")
                    currentHeartRate.value = bpm
                    // Enviar la frecuencia cardíaca al teléfono automáticamente en tiempo real
                    lifecycleScope.launch {
                        HealthDataService.enviarFCDirectamente(applicationContext, bpm)
                    }
                }
            }

            // Enviar los pasos al teléfono automáticamente si se reciben
            val stepsData = data.getData(DataType.STEPS_DAILY)
            stepsData.forEach { dataPoint ->
                val value = dataPoint.value
                val pasos = when (value) {
                    is Long -> value.toInt()
                    is Double -> value.toInt()
                    is Int -> value
                    else -> value.toString().toIntOrNull() ?: 0
                }
                Log.d("MainActivity", "Pasos recibidos: $pasos")
                lifecycleScope.launch {
                    HealthDataService.enviarPasosDirectamente(applicationContext, pasos)
                }
            }
        }
    }

    private val permissionsToRequest: Array<String>
        get() {
            val list = mutableListOf(Manifest.permission.ACTIVITY_RECOGNITION)
            if (android.os.Build.VERSION.SDK_INT >= 36) {
                list.add("android.permission.health.READ_HEART_RATE")
            } else {
                list.add(Manifest.permission.BODY_SENSORS)
            }
            return list.toTypedArray()
        }

    private fun checkPermissionsGranted(): Boolean {
        return permissionsToRequest.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissionsToRequest.all { permission ->
            permissions[permission] == true
        }
        if (allGranted) {
            Log.d("MainActivity", "Todos los permisos necesarios concedidos.")
            lifecycleScope.launch {
                HealthDataService.registrar(applicationContext)
                iniciarMedicion()
            }
        } else {
            Log.w("MainActivity", "Algunos permisos requeridos fueron denegados.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        // Verificar y solicitar permisos de forma dinámica al iniciar
        verificarYSolicitarPermisos()

        setContent {
            val heartRate by currentHeartRate
            WearApp(heartRate = heartRate)
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissionsGranted()) {
            iniciarMedicion()
        }
    }


    private fun verificarYSolicitarPermisos() {
        if (checkPermissionsGranted()) {
            lifecycleScope.launch {
                HealthDataService.registrar(applicationContext)
                iniciarMedicion()
            }
        } else {
            Log.d("MainActivity", "Solicitando permisos necesarios...")
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    override fun onStop() {
        super.onStop()
        detenerMedicion()
    }

    private fun iniciarMedicion() {
        if (isMeasuring) return
        try {
//            measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, measureCallback)
            isMeasuring = true
            Log.d("MainActivity", "Monitoreo de MeasureClient iniciado")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al registrar MeasureCallback: ${e.message}")
        }
    }

    private fun detenerMedicion() {
        if (!isMeasuring) return
        try {
            measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, measureCallback)
            isMeasuring = false
            Log.d("MainActivity", "Monitoreo de MeasureClient detenido")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al desregistrar MeasureCallback: ${e.message}")
        }
    }
}

@Composable
fun WearApp(heartRate: Int) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    SmartHealthMonitorTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "SmartHealth", style = MaterialTheme.typography.caption1)
                
                if (heartRate > 0) {
                    Text(text = "$heartRate BPM", style = MaterialTheme.typography.title1)
                    Text(text = "Listo para enviar", style = MaterialTheme.typography.caption2)
                } else {
                    Text(text = "Midiendo...", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text(
                        text = "Ajusta la FC en el emulador", 
                        style = MaterialTheme.typography.caption2, 
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Chip(
                        onClick = {
                            scope.launch {
                                val bpmToSend = if (heartRate > 0) heartRate else (60..120).random()
                                HealthDataService.enviarFCDirectamente(context, bpmToSend)
                            }
                        },
                        label = { Text("Enviar FC") },
                        colors = ChipDefaults.primaryChipColors()
                    )

                    Chip(
                        onClick = {
                            scope.launch {
                                val pasosToSend = (1000..10000).random()
                                HealthDataService.enviarPasosDirectamente(context, pasosToSend)
                            }
                        },
                        label = { Text("Pasos") },
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }
    }
}

@Composable
fun SmartHealthMonitorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}