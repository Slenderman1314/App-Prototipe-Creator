package app.prototype.creator.utils

import app.prototype.creator.BuildConfig

/**
 * Android implementation: Get property from BuildConfig
 */
actual fun getEnvironmentProperty(key: String): String? {
    return try {
        val field = BuildConfig::class.java.getDeclaredField(key)
        field.get(null) as? String
    } catch (e: Exception) {
        null
    }
}
