package app.prototype.creator.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

/**
 * Application settings manager
 */
object AppSettings {
    private var _settings: Settings? = null
    
    /**
     * Get the settings instance
     */
    fun getSettings(): Settings {
        return _settings ?: createSettings().also { _settings = it }
    }
    
    
    /**
     * Create a new settings instance
     */
    private fun createSettings(): Settings {
        return Settings()
    }
    
    /**
     * Clear all settings (for testing/logout)
     */
    fun clear() {
        _settings?.let { settings ->
            settings.keys.forEach { key ->
                settings.remove(key)
            }
        }
    }
}

/**
 * Extension function to get a string setting with a default value
 */
fun Settings.getString(key: String, defaultValue: String = ""): String {
    return get(key, defaultValue)
}

/**
 * Extension function to set a string setting
 */
fun Settings.setString(key: String, value: String) {
    this[key] = value
}

/**
 * Extension function to get a boolean setting with a default value
 */
fun Settings.getBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return get(key, defaultValue)
}

/**
 * Extension function to set a boolean setting
 */
fun Settings.setBoolean(key: String, value: Boolean) {
    this[key] = value
}

/**
 * Extension function to get an int setting with a default value
 */
fun Settings.getInt(key: String, defaultValue: Int = 0): Int {
    return get(key, defaultValue)
}

/**
 * Extension function to set an int setting
 */
fun Settings.setInt(key: String, value: Int) {
    this[key] = value
}
