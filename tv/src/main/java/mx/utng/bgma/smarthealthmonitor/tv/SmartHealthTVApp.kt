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
    }
}
