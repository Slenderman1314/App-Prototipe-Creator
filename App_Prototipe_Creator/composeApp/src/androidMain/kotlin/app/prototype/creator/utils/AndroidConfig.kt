package app.prototype.creator.utils

import android.content.Context
import io.github.aakira.napier.Napier
import java.io.File
import java.util.Properties

/**
 * Handles Android-specific configuration loading.
 * Loads configuration from multiple sources with the following priority:
 * 1. local.properties (development)
 * 2. BuildConfig (build-time)
 * 3. assets/config.properties (packaged config)
 * 4. System environment variables (runtime)
 */
object AndroidConfig {
    private var isInitialized = false
    private val properties = mutableMapOf<String, String>()
    
    /**
     * Initialize the configuration by loading from all available sources.
     * @param context Android context
     * @return Map of all loaded properties
     */
    fun initialize(context: Context): Map<String, String> {
        if (isInitialized) return properties.toMap()
        
        try {
            // 1. Load from local.properties (for development)
            try {
                val localProperties = File("${context.filesDir.parent}/local.properties")
                if (localProperties.exists()) {
                    localProperties.inputStream().use { input ->
                        val props = Properties()
                        props.load(input)
                        props.forEach { (key, value) ->
                            val keyStr = key.toString()
                            if (keyStr.startsWith("n8n.") || keyStr.startsWith("N8N_") ||
                                keyStr.startsWith("supabase.") || keyStr.startsWith("SUPABASE_")) {
                                val normalizedKey = keyStr.uppercase().replace('.', '_')
                                properties[normalizedKey] = value.toString()
                                Napier.d("✅ Loaded $normalizedKey from local.properties")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Napier.e("⚠️ Could not load from local.properties: ${e.message}")
            }
            
            // 2. Load from BuildConfig (build-time)
            try {
                val buildConfigClass = Class.forName("${context.packageName}.BuildConfig")
                val fields = buildConfigClass.declaredFields
                
                fields.forEach { field ->
                    val name = field.name
                    if (name.startsWith("N8N_") || name.startsWith("SUPABASE_")) {
                        val value = field.get(null) as? String
                        if (!value.isNullOrEmpty()) {
                            properties[name] = value
                            Napier.d("✅ Loaded $name from BuildConfig")
                        }
                    }
                }
            } catch (e: Exception) {
                Napier.e("⚠️ Could not load from BuildConfig: ${e.message}")
            }
            
            // 3. Load from assets/config.properties
            try {
                context.assets.open("config.properties").use { input ->
                    val props = Properties()
                    props.load(input)
                    props.forEach { (key, value) ->
                        val keyStr = key.toString()
                        if (keyStr.startsWith("N8N_") || keyStr.startsWith("SUPABASE_")) {
                            properties[keyStr] = value.toString()
                            Napier.d("✅ Loaded $keyStr from assets/config.properties")
                        }
                    }
                }
            } catch (e: Exception) {
                Napier.e("⚠️ Could not load from assets: ${e.message}")
            }
            
            // 4. Load from system environment variables
            System.getenv().forEach { (key, value) ->
                if (key.startsWith("N8N_") || key.startsWith("SUPABASE_")) {
                    properties[key] = value
                    Napier.d("✅ Loaded $key from system environment")
                }
            }
            
            // 5. Set default values if not set
            if (!properties.containsKey("N8N_BASE_URL")) {
                // Usar 10.0.2.2 para el emulador Android (apunta a localhost de la máquina host)
                val defaultUrl = "http://10.0.2.2:5678"
                properties["N8N_BASE_URL"] = defaultUrl
                Napier.w("⚠️ N8N_BASE_URL not found, using default: $defaultUrl")
            }
            
            if (!properties.containsKey("N8N_WEBHOOK_PATH")) {
                properties["N8N_WEBHOOK_PATH"] = "/webhook"
                Napier.w("⚠️ N8N_WEBHOOK_PATH not found, using default: /webhook")
            }
            
            if (!properties.containsKey("SUPABASE_URL")) {
                Napier.e("❌ SUPABASE_URL not found in any configuration source")
            }
            
            if (!properties.containsKey("SUPABASE_ANON_KEY")) {
                Napier.e("❌ SUPABASE_ANON_KEY not found in any configuration source")
            }
            
            isInitialized = true
            return properties.toMap()
        } catch (e: Exception) {
            Napier.e("❌ Error initializing AndroidConfig: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * Get a property value by key
     * @param key The property key to retrieve
     * @return The property value or null if not found
     */
    fun getProperty(key: String): String? {
        return properties[key]
    }
}
