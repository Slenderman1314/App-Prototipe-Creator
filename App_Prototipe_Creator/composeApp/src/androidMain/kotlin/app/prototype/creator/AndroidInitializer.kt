package app.prototype.creator

import android.content.Context
import app.prototype.creator.data.service.PlatformExporter
import io.github.aakira.napier.Napier

// Global context holder for PlatformExporter
private var _context: Context? = null

/**
 * Initialize Android-specific components
 */
fun initializeAndroid(context: Context) {
    try {
        // Store context globally for PlatformExporter
        _context = context
        PlatformExporter.context = context
        Napier.d("✅ PlatformExporter context initialized: ${context.packageName}")
    } catch (e: Exception) {
        Napier.e("❌ Error initializing Android components", e)
        e.printStackTrace()
    }
}

/**
 * Get the stored context
 */
fun getApplicationContext(): Context? = _context
