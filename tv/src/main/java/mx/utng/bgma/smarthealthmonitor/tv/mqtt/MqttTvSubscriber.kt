package mx.utng.bgma.smarthealthmonitor.tv.mqtt

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import mx.edu.utng.bgma.smarthealthmonitor.mqtt.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttTvSubscriber(
    private val context : Context,
    private val tvFlow  : MutableStateFlow<TvMessage?>   // actualiza el ViewModel
) {
    private var client: MqttAsyncClient? = null

    fun connect() {
        client = MqttAsyncClient(
            MqttConfig.BROKER_URL,
            MqttConfig.CLIENT_TV,
            MemoryPersistence()
        )

        client?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String, msg: MqttMessage) {
                try {
                    val payloadStr = String(msg.payload)
                    if (topic == MqttConfig.TOPIC_TV) {
                        val tvMsg = Json.decodeFromString<TvMessage>(payloadStr)
                        tvFlow.value = tvMsg
                        android.util.Log.d("MQTT_TV","📺 Recibido de TV: ${tvMsg.bpm} bpm")
                    } else if (topic == MqttConfig.TOPIC_FC) {
                        val fcMsg = Json.decodeFromString<FcMessage>(payloadStr)
                        val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        val horaStr = sdf.format(java.util.Date(fcMsg.timestamp))
                        tvFlow.value = TvMessage(bpm = fcMsg.bpm, estado = fcMsg.estado, hora = horaStr)
                        android.util.Log.d("MQTT_TV","📺 Recibido directo del Reloj: ${fcMsg.bpm} bpm")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MQTT_TV", "❌ Error al decodificar: ${e.message}")
                }
            }
            override fun connectionLost(cause: Throwable?) {
                android.util.Log.w("MQTT_TV", "Conexión perdida con el broker: ${cause?.message}")
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        val options = MqttConnectOptions().apply {
            userName = MqttConfig.USERNAME
            password = MqttConfig.PASSWORD.toCharArray()
            isCleanSession = true
            socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
        }

        client?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                client?.subscribe(MqttConfig.TOPIC_TV, MqttConfig.QOS)
                client?.subscribe(MqttConfig.TOPIC_FC, MqttConfig.QOS)
                android.util.Log.d("MQTT_TV","✅ TV suscrita a ${MqttConfig.TOPIC_TV} y ${MqttConfig.TOPIC_FC}")
            }
            override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                android.util.Log.e("MQTT_TV","❌ Error de conexión MQTT", ex)
            }
        })
    }
    fun disconnect() { client?.disconnect() }
}
