// tv/src/main/java/mx/utng/bgma/smarthealthmonitor/tv/SmartHealthTVApp.kt
package mx.utng.bgma.smarthealthmonitor.tv

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC

/**
 * Application del módulo TV.
 *
 * CRÍTICO: SmartHealthRepository.init(context) DEBE llamarse aquí,
 * antes de que cualquier ViewModel intente usar el DAO de Room.
 * Sin esta inicialización la app crashea con NullPointerException
 * cuando TvViewModel intenta colectar el historial.
 *
 * Registrada en AndroidManifest.xml con android:name=".SmartHealthTVApp"
 */
class SmartHealthTVApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializar Room a través del repositorio singleton
        SmartHealthRepository.init(this)

        // Insertar datos de prueba para comprobar la Base de Datos local en la TV
        CoroutineScope(Dispatchers.IO).launch {
            // Insertar lecturas para que la fila de historial en la TV muestre datos reactivos de Room
            SmartHealthRepository.actualizarFC(72)
            SmartHealthRepository.actualizarFC(85)
            SmartHealthRepository.actualizarFC(105) // Alerta (rojo)
        }

        // Iniciar hilo UDP en la TV para recibir datos en vivo del emulador del teléfono
        iniciarServidorUDPTV()
    }

    private fun iniciarServidorUDPTV() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Escuchamos en el puerto 8888
                val socket = java.net.DatagramSocket(8888)
                val buf = ByteArray(1024)
                android.util.Log.d("TV_DATO", "Servidor UDP de la TV iniciado en puerto 8888")
                
                while (true) {
                    val packet = java.net.DatagramPacket(buf, buf.size)
                    socket.receive(packet)
                    val mensaje = String(packet.data, 0, packet.length)
                    android.util.Log.d("TV_DATO", "UDP Recibido en TV: $mensaje")

                    val partes = mensaje.split(":")
                    if (partes.size == 2) {
                        val tipo = partes[0]
                        val valor = partes[1].toIntOrNull() ?: continue

                        when (tipo) {
                            "fc" -> {
                                SmartHealthRepository.actualizarFC(valor)
                            }
                            "pasos" -> {
                                SmartHealthRepository.actualizarPasos(valor)
                            }
                            "spo2" -> {
                                SmartHealthRepository.actualizarSpO2(valor)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("TV_DATO", "Error en Servidor UDP de TV: ${e.message}")
            }
        }
    }
}
