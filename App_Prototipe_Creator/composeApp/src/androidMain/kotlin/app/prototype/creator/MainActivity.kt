package app.prototype.creator

import android.app.ActivityManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.prototype.creator.di.initKoin
import app.prototype.creator.utils.loadEnvFromGradleProperties
import io.github.aakira.napier.Napier
import io.github.aakira.napier.DebugAntilog
import app.prototype.creator.initializeAndroid
import app.prototype.creator.ActivityProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityProvider.current = this
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        android.util.Log.d("SECURE_FLAG", "🔒 BUILD v4: FLAG_SECURE + recents protection applied")

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val taskDesc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityManager.TaskDescription.Builder()
                    .setBackgroundColor(Color.BLACK)
                    .build()
            } else {
                ActivityManager.TaskDescription(null, null, Color.BLACK)
            }
            setTaskDescription(taskDesc)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setRecentsScreenshotEnabled(false)
            android.util.Log.d("SECURE_FLAG", "🔒 setRecentsScreenshotEnabled(false) applied (API 33+)")
        }

        enableEdgeToEdge()
        
        try {
            // Initialize Napier for logging FIRST
            Napier.base(DebugAntilog())
            Napier.d("🚀 MainActivity onCreate started")

            // Initialize Android components
            initializeAndroid(applicationContext)
            
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

    override fun onDestroy() {
        super.onDestroy()
        ActivityProvider.current = null
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
