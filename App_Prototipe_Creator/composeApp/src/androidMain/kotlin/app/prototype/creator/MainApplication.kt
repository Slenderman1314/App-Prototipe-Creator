package app.prototype.creator

import android.app.Application
import app.prototype.creator.di.initKoin
import app.prototype.creator.utils.loadEnvFromGradleProperties
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging
        Napier.base(DebugAntilog())
        
        // Load environment variables from assets
        loadEnvFromGradleProperties(this)
        
        // Initialize Koin
        try {
            initKoin()
            Napier.d("✅ Koin initialized successfully")
        } catch (e: Exception) {
            Napier.e("❌ Error initializing Koin", e)
            e.printStackTrace()
        }
    }
}
