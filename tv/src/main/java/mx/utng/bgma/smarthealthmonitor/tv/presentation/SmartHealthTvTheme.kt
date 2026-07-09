package mx.utng.bgma.smarthealthmonitor.tv.presentation

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SmartHealthTvTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFF1B4F8A),
        secondary = Color(0xFFD4860A),
        background = Color(0xFF0D1117),
        error = Color(0xFFB3261E)
    )
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
