package mx.edu.utng.bgma.smarthealthmonitor.wear

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.health.services.client.data.SampleDataPoint
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.*

class HealthDataService : PassiveListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var messageClient: MessageClient
    private val TAG = "HealthDataService"

    override fun onCreate() {
        super.onCreate()
        messageClient = Wearable.getMessageClient(this)
        Log.d(TAG, "HealthDataService creado")
    }

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        val fcDataPoints = dataPoints.getData(DataType.HEART_RATE_BPM)
        fcDataPoints.forEach { dataPoint ->
            if (dataPoint is SampleDataPoint<Double>) {
                val bpm = dataPoint.value.toInt()
                Log.d(TAG, "FC recibida: $bpm BPM")
                scope.launch {
                    enviarFC(bpm)
                }
            }
        }

        val stepsDataPoints = dataPoints.getData(DataType.STEPS_DAILY)
        Log.d("MiAppDebug", "Contenido de pasos: $stepsDataPoints")
        stepsDataPoints.forEach { dataPoint ->
            val value = dataPoint.value
            val pasos = when (value) {
                is Long -> value.toInt()
                is Double -> value.toInt()
                is Int -> value
                else -> value.toString().toIntOrNull() ?: 0
            }
            Log.d(TAG, "Pasos recibidos: $pasos")
            scope.launch {
                enviarPasos(pasos)
            }
        }
    }

    private suspend fun enviarFC(bpm: Int) {
        try {
            val data = bpm.toString().toByteArray()
            val nodeClient = Wearable.getNodeClient(this)
            val nodes = Tasks.await(nodeClient.connectedNodes)
            for (node in nodes) {
                messageClient.sendMessage(
                    node.id,
                    "/smarthealthmonitor/fc",
                    data
                )
                Log.d(TAG, "FC enviada al nodo ${node.displayName} (${node.id}): $bpm")
            }
            if (nodes.isEmpty()) {
                Log.w(TAG, "No hay nodos conectados para enviar la FC.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando FC: ${e.message}")
        }
    }

    private suspend fun enviarPasos(pasos: Int) {
        try {
            val data = pasos.toString().toByteArray()
            val nodeClient = Wearable.getNodeClient(this)
            val nodes = Tasks.await(nodeClient.connectedNodes)
            for (node in nodes) {
                messageClient.sendMessage(
                    node.id,
                    "/smarthealthmonitor/pasos",
                    data
                )
                Log.d(TAG, "Pasos enviados al nodo ${node.displayName} (${node.id}): $pasos")
            }
            if (nodes.isEmpty()) {
                Log.w(TAG, "No hay nodos conectados para enviar pasos.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando pasos: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        Log.d(TAG, "HealthDataService destruido")
    }

    companion object {
        suspend fun registrar(context: Context) {
            try {
                val hsClient = HealthServices.getClient(context)
                val passiveClient = hsClient.passiveMonitoringClient

                val config = PassiveListenerConfig.builder()
                    .setDataTypes(setOf(
                        DataType.HEART_RATE_BPM,
                        DataType.STEPS_DAILY
                    ))
                    .setShouldUserActivityInfoBeRequested(true)
                    .build()

                passiveClient.setPassiveListenerServiceAsync(
                    HealthDataService::class.java,
                    config
                )
                Log.d("HealthDataService", "Health Services registrado correctamente")
            } catch (e: Exception) {
                Log.e("HealthDataService", "Error registrando: ${e.message}")
            }
        }

        suspend fun enviarFCDirectamente(context: Context, bpm: Int) = withContext(Dispatchers.IO) {
            try {
                val messageClient = Wearable.getMessageClient(context)
                val nodeClient = Wearable.getNodeClient(context)
                val data = bpm.toString().toByteArray()
                val nodes = Tasks.await(nodeClient.connectedNodes)
                for (node in nodes) {
                    messageClient.sendMessage(
                        node.id,
                        "/smarthealthmonitor/fc",
                        data
                    )
                    Log.d("HealthDataService", "FC manual enviada al nodo ${node.displayName}: $bpm")
                }
                if (nodes.isEmpty()) {
                    Log.w("HealthDataService", "No hay nodos conectados para enviar FC manual.")
                }
            } catch (e: Exception) {
                Log.e("HealthDataService", "Error enviando FC manual: ${e.message}")
            }
        }

        suspend fun enviarPasosDirectamente(context: Context, pasos: Int) = withContext(Dispatchers.IO) {
            try {
                val messageClient = Wearable.getMessageClient(context)
                val nodeClient = Wearable.getNodeClient(context)
                val data = pasos.toString().toByteArray()
                val nodes = Tasks.await(nodeClient.connectedNodes)
                for (node in nodes) {
                    messageClient.sendMessage(
                        node.id,
                        "/smarthealthmonitor/pasos",
                        data
                    )
                    Log.d("HealthDataService", "Pasos manuales enviados al nodo ${node.displayName}: $pasos")
                }
                if (nodes.isEmpty()) {
                    Log.w("HealthDataService", "No hay nodos conectados para enviar pasos manuales.")
                }
            } catch (e: Exception) {
                Log.e("HealthDataService", "Error enviando pasos manual: ${e.message}")
            }
        }
    }
}