package app.prototype.creator.utils

import io.github.aakira.napier.Napier

/**
 * Firebase configuration object
 * 
 * Manages Firebase configuration from environment variables
 */
object FirebaseConfig {
    val projectId: String by lazy {
        getEnvOrDefault("FIREBASE_PROJECT_ID", "")
    }

    val apiKey: String by lazy {
        getEnvOrDefault("FIREBASE_API_KEY", "")
    }

    val authDomain: String by lazy {
        getEnvOrDefault("FIREBASE_AUTH_DOMAIN", "")
    }

    val storageBucket: String by lazy {
        getEnvOrDefault("FIREBASE_STORAGE_BUCKET", "")
    }

    val messagingSenderId: String by lazy {
        getEnvOrDefault("FIREBASE_MESSAGING_SENDER_ID", "")
    }

    val appId: String by lazy {
        getEnvOrDefault("FIREBASE_APP_ID", "")
    }

    /**
     * Database mode: LOCAL, CLOUD, or HYBRID
     */
    val databaseMode: String by lazy {
        getEnvOrDefault("DATABASE_MODE", "LOCAL").uppercase()
    }

    /**
     * Check if Firebase is configured
     */
    val isConfigured: Boolean by lazy {
        projectId.isNotEmpty() && apiKey.isNotEmpty()
    }

    /**
     * Check if cloud mode is enabled
     */
    val isCloudEnabled: Boolean by lazy {
        databaseMode == "CLOUD" || databaseMode == "HYBRID"
    }

    /**
     * Check if local mode is enabled
     */
    val isLocalEnabled: Boolean by lazy {
        databaseMode == "LOCAL" || databaseMode == "HYBRID"
    }

    private fun getEnvOrDefault(key: String, default: String): String {
        return try {
            System.getenv(key) ?: default
        } catch (e: Exception) {
            Napier.w("Failed to get environment variable: $key, using default: $default")
            default
        }
    }

    /**
     * Log current configuration (without sensitive data)
     */
    fun logConfiguration() {
        Napier.d("""
            === Firebase Configuration ===
            Project ID: ${if (projectId.isNotEmpty()) "[SET]" else "[NOT SET]"}
            API Key: ${if (apiKey.isNotEmpty()) "[SET]" else "[NOT SET]"}
            Auth Domain: ${if (authDomain.isNotEmpty()) "[SET]" else "[NOT SET]"}
            Database Mode: $databaseMode
            Is Configured: $isConfigured
            Cloud Enabled: $isCloudEnabled
            Local Enabled: $isLocalEnabled
            ==============================
        """.trimIndent())
    }
}
