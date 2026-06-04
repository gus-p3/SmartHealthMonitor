package mx.edu.utng.bgma.smarthealthmonitor.data

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WearListenerService : WearableListenerService() {

    // Scope para ejecutar las funciones suspendidas del repositorio
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val PATH_FC    = "/smarthealthmonitor/fc"
        const val PATH_PASOS = "/smarthealthmonitor/pasos"
        const val PATH_SPO2  = "/smarthealthmonitor/spo2"
        private const val TAG = "WearListener"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data   = String(messageEvent.data)
        val path   = messageEvent.path
        Log.d(TAG, "Mensaje recibido: path=$path, data=$data")

        // Usamos el scope para llamar a las funciones suspend (actualizarFC, etc)
        serviceScope.launch {
            when (path) {
                PATH_FC -> {
                    val bpm = data.toIntOrNull() ?: return@launch
                    SmartHealthRepository.actualizarFC(bpm)
                }
                PATH_PASOS -> {
                    val pasos = data.toIntOrNull() ?: return@launch
                    SmartHealthRepository.actualizarPasos(pasos)
                }
                PATH_SPO2 -> {
                    val spo2 = data.toIntOrNull() ?: return@launch
                    SmartHealthRepository.actualizarSpO2(spo2)
                }
                else -> Log.w(TAG, "Path desconocido: $path")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Cancelamos el scope al destruir el servicio
    }
}
