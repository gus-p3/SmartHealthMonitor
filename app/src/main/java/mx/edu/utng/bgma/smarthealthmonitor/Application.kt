package mx.edu.utng.bgma.smarthealthmonitor

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository

class SmartHealthApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)  // inicializar Room

        applicationScope.launch(Dispatchers.IO) {
            SmartHealthRepository.limpiarHistorialAntiguo()
        }
    }
}
