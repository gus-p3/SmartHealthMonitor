package mx.edu.utng.bgma.smarthealthmonitor

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.mqtt.MqttAppService

class SmartHealthApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var mqttService: MqttAppService

    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)  // inicializar Room
        mx.edu.utng.bgma.smarthealthmonitor.data.sync.NeonSyncWorker.schedule(this) // Programar sync periódico con Neon

        applicationScope.launch(Dispatchers.IO) {
            SmartHealthRepository.limpiarHistorialAntiguo()
        }
        mqttService = MqttAppService(context = this)
        mqttService.connect()
    }


}
