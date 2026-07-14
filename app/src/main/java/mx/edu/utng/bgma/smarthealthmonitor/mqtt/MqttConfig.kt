package mx.edu.utng.bgma.smarthealthmonitor.mqtt
import mx.edu.utng.bgma.smarthealthmonitor.BuildConfig

object MqttConfig {
    val BROKER_URL: String = BuildConfig.HIVEMQ_BROKER_URL
    val USERNAME:   String = BuildConfig.HIVEMQ_USERNAME
    val PASSWORD:   String = BuildConfig.HIVEMQ_PASSWORD
    const val TOPIC_FC    = "utng/smarthealthmonitor/fc"
    const val TOPIC_TV    = "utng/smarthealthmonitor/tv"
    const val TOPIC_ALERT = "utng/smarthealthmonitor/alerta"
    const val QOS = 1
    const val CLIENT_WEAR = "smarthealthmonitor-wear"
    const val CLIENT_APP  = "smarthealthmonitor-app"
    const val CLIENT_TV   = "smarthealthmonitor-tv"
}