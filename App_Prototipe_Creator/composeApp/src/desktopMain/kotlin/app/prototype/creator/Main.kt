package app.prototype.creator

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {
    // Initialize Napier for Desktop
    Napier.base(DebugAntilog())
    Napier.d("🖥️ Starting Desktop application...")
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "App Prototype Creator",
        state = rememberWindowState(width = 1200.dp, height = 800.dp)
    ) {
        App()
    }
}
