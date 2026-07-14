package mx.edu.utng.bgma.smarthealthmonitor.mqtt

import mx.edu.utng.bgma.smarthealthmonitor.shared.BuildConfig

/**
 * Configuración MQTT compartida entre módulos app y wear2.
 * Las credenciales se leen desde shared/BuildConfig,
 * que a su vez las obtiene de local.properties (nunca en git).
 */
object MqttConfig {

    val BROKER_URL: String = BuildConfig.HIVEMQ_BROKER_URL
    val USERNAME:   String = BuildConfig.HIVEMQ_USERNAME
    val PASSWORD:   String = BuildConfig.HIVEMQ_PASSWORD

    // Topics del proyecto
    const val TOPIC_FC    = "utng/smarthealthmonitor/fc"
    const val TOPIC_TV    = "utng/smarthealthmonitor/tv"
    const val TOPIC_ALERT = "utng/smarthealthmonitor/alerta"

    // QoS: 0=best effort, 1=at least once, 2=exactly once
    const val QOS = 1

    // Client IDs únicos por dispositivo
    const val CLIENT_WEAR = "smarthealthmonitor-wear"
    const val CLIENT_APP  = "smarthealthmonitor-app"
    const val CLIENT_TV   = "smarthealthmonitor-tv"
}
