package mx.utng.bgma.smarthealthmonitor.tv

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository

class WearListenerService : WearableListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val PATH_FC    = "/smarthealthmonitor/fc"
        const val PATH_PASOS = "/smarthealthmonitor/pasos"
        const val PATH_SPO2  = "/smarthealthmonitor/spo2"
        private const val TAG = "WearListenerTV"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data   = String(messageEvent.data)
        val path   = messageEvent.path
        Log.d(TAG, "Mensaje recibido en TV: path=$path, data=$data")

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
                else -> Log.w(TAG, "Path desconocido en TV: $path")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
