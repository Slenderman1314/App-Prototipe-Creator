package app.prototype.creator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.prototype.creator.di.initKoin
import app.prototype.creator.utils.loadEnvFromGradleProperties
import io.github.aakira.napier.Napier
import io.github.aakira.napier.DebugAntilog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        try {
            // Initialize Napier for logging FIRST
            Napier.base(DebugAntilog())
            Napier.d("🚀 MainActivity onCreate started")
            
            // Load environment variables from env.properties in assets
            loadEnvFromGradleProperties(applicationContext)
            Napier.d("✅ Environment variables loaded")
            
            // Initialize Koin with Android context
            initKoin(applicationContext)
            
            Napier.d("✅ All initialization complete")
        } catch (e: Exception) {
            Napier.e("❌ Error during initialization", e)
            e.printStackTrace()
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}