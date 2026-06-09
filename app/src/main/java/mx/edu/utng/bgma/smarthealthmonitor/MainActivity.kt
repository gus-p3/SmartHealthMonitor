package mx.edu.utng.bgma.smarthealthmonitor

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utng.bgma.smarthealthmonitor.ui.navegation.SmartHealthNavGraph
import mx.edu.utng.bgma.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            SmartHealthMonitorTheme {
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    LoginScreen(
//                        onLoginSuccess = {
//                            // TODO sesión 5: navegar al Dashboard
//                            Log.d("SmartHealth", "Login exitoso")
//                        }
//                    )
//                }
//            }
//        }
        setContent {
            // NavGraph es ahora el punto de entrada — no LoginScreen directamente
            SmartHealthMonitorTheme {
                SmartHealthNavGraph()
            }
        }
    }
}

// 1. Extraemos la UI a una función Composable separada
@Composable
fun MainScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "SmartHealth Monitor",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(24.dp)
        )
    }
}

// 2. Colocamos el Preview en el nivel superior (fuera de la clase)
@Preview(showBackground = true, name = "Light")
@Preview(
    showBackground = true,
    name = "Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ThemePreview() {
    SmartHealthMonitorTheme {
        MainScreen()
    }
}


@Preview(
    name = "Login - Light", showBackground = true,
    showSystemUi = true, device = "id:pixel_6"
)
@Preview(
    name = "Login - Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Login - Big Font", showBackground = true,
    fontScale = 1.5f
)
@Composable
private fun LoginScreenPreview() {
    SmartHealthMonitorTheme {
        LoginScreen()
    }
}
