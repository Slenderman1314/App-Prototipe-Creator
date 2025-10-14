package app.prototype.creator

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.prototype.creator.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import java.io.File

fun main() = application {
    // Inicializar logging
    Napier.base(DebugAntilog())
    
    // Cargar variables de entorno desde .env
    loadEnvFile()
    
    // Inicializar Koin
    try {
        initKoin()
        Napier.d("✅ Koin initialized successfully")
    } catch (e: Exception) {
        Napier.e("❌ Error initializing Koin", e)
        e.printStackTrace()
    }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "App Prototype Creator",
        state = rememberWindowState(width = 1200.dp, height = 800.dp)
    ) {
        // Usar la aplicación completa
        App()
    }
}

/**
 * Carga las variables de entorno desde el archivo .env
 */
fun loadEnvFile() {
    try {
        // Intentar múltiples ubicaciones
        val possiblePaths = listOf(
            File(".env"),
            File("../.env"),
            File("../../.env")
        )
        
        val envFile = possiblePaths.firstOrNull { it.exists() }
        
        if (envFile != null) {
            Napier.d("📄 Loading .env file from: ${envFile.absolutePath}")
            envFile.readLines().forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                    val parts = trimmedLine.split("=", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim()
                        System.setProperty(key, value)
                        Napier.d("✓ Loaded: $key")
                    }
                }
            }
            Napier.d("✅ .env file loaded successfully")
        } else {
            Napier.w("⚠️ .env file not found in any of these locations:")
            possiblePaths.forEach { Napier.w("  - ${it.absolutePath}") }
        }
    } catch (e: Exception) {
        Napier.e("❌ Error loading .env file", e)
    }
}
