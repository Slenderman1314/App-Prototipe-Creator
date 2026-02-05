package app.prototype.creator.utils

import io.github.aakira.napier.Napier

/**
 * Platform-specific property getter
 */
expect fun getEnvironmentProperty(key: String): String?

/**
 * Application configuration loaded from environment variables.
 * For multiplatform compatibility, environment variables should be set at runtime.
 */
object Config {
    // Debug mode flag
    val DEBUG = true
    
    // API Configuration
    val API_URL: String = "https://api.example.com"
    val API_TIMEOUT: Long = 30_000L
    
    // Database configuration
    val databaseName: String = "prototype_db"
    
    private fun getRequiredProperty(key: String): String {
        return getEnvironmentProperty(key)?.takeIf { it.isNotBlank() } ?: run {
            val error = "$key property is not set or is empty. Please check your .env file or BuildConfig."
            Napier.e(error)
            throw IllegalStateException(error)
        }
    }

    // Backend Type Selection (N8N or SPRING_BOOT)
    val aiBackendType: String = getEnvironmentProperty("AI_BACKEND_TYPE")?.uppercase() ?: "N8N"
    
    // n8n configuration
    val n8nBaseUrl: String by lazy {
        if (aiBackendType == "N8N") {
            getRequiredProperty("N8N_BASE_URL").also {
                Napier.d("ℹ️ n8n Base URL: ${it.substring(0, minOf(10, it.length))}...")
            }
        } else {
            ""
        }
    }
    
    val n8nWebhookPath: String by lazy {
        if (aiBackendType == "N8N") {
            getRequiredProperty("N8N_WEBHOOK_PATH")
        } else {
            ""
        }
    }
    val n8nApiKey: String? = getEnvironmentProperty("N8N_API_KEY")?.takeIf { it.isNotBlank() }
    
    // Spring Boot AI configuration
    val springBootBaseUrl: String = getEnvironmentProperty("SPRING_BOOT_BASE_URL") 
        ?: "http://localhost:8080"
    val springBootApiKey: String? = getEnvironmentProperty("SPRING_BOOT_API_KEY")?.takeIf { it.isNotBlank() }
    
    // Firebase configuration
    val firebaseProjectId: String = getEnvironmentProperty("FIREBASE_PROJECT_ID")?.takeIf { it.isNotBlank() } ?: "".also {
        Napier.w("⚠️ FIREBASE_PROJECT_ID not configured")
    }
    
    val firebaseApiKey: String = getEnvironmentProperty("FIREBASE_API_KEY")?.takeIf { it.isNotBlank() } ?: "".also {
        Napier.w("⚠️ FIREBASE_API_KEY not configured")
    }
    
    val firebaseAuthDomain: String = getEnvironmentProperty("FIREBASE_AUTH_DOMAIN")?.takeIf { it.isNotBlank() } ?: ""
    val firebaseStorageBucket: String = getEnvironmentProperty("FIREBASE_STORAGE_BUCKET")?.takeIf { it.isNotBlank() } ?: ""
    val firebaseMessagingSenderId: String = getEnvironmentProperty("FIREBASE_MESSAGING_SENDER_ID")?.takeIf { it.isNotBlank() } ?: ""
    val firebaseAppId: String = getEnvironmentProperty("FIREBASE_APP_ID")?.takeIf { it.isNotBlank() } ?: ""
    
    // Database mode: LOCAL, CLOUD, or HYBRID
    val databaseMode: String = getEnvironmentProperty("DATABASE_MODE")?.uppercase() ?: "LOCAL".also {
        Napier.d("ℹ️ DATABASE_MODE not configured, using default: LOCAL")
    }
    
    // Check if Firebase is configured
    val isFirebaseConfigured: Boolean = firebaseProjectId.isNotEmpty() && firebaseApiKey.isNotEmpty()
    
    // Check if cloud mode is enabled
    val isCloudEnabled: Boolean = (databaseMode == "CLOUD" || databaseMode == "HYBRID") && isFirebaseConfigured
    
    // Check if local mode is enabled
    val isLocalEnabled: Boolean = databaseMode == "LOCAL" || databaseMode == "HYBRID" || !isFirebaseConfigured
    
    // Theme and language configuration
    val defaultTheme: String = "system"
    val defaultLanguage: String = "es"
    
    // Complete URLs for n8n endpoints
    val n8nChatEndpoint: String
        get() {
            if (aiBackendType != "N8N") return ""
            return "${n8nBaseUrl.trimEnd('/')}/${n8nWebhookPath.trimStart('/')}"
        }
    
    init {
        // Log configuration summary in debug mode
        if (DEBUG) {
            val n8nBaseUrlEnv = getEnvironmentProperty("N8N_BASE_URL")
            val n8nWebhookPathEnv = getEnvironmentProperty("N8N_WEBHOOK_PATH")
            val configSummary = """
                |=== Configuration Summary ===
                |DEBUG: $DEBUG
                |API_URL: $API_URL
                |API_TIMEOUT: $API_TIMEOUT ms
                |AI_BACKEND_TYPE: $aiBackendType
                |N8N_BASE_URL: ${if (!n8nBaseUrlEnv.isNullOrBlank()) "[SET]" else "[NOT SET]"}
                |N8N_WEBHOOK_PATH: ${if (!n8nWebhookPathEnv.isNullOrBlank()) "[SET]" else "[NOT SET]"}
                |N8N_API_KEY: ${if (n8nApiKey?.isNotBlank() == true) "[SET]" else "[NOT SET]"}
                |SPRING_BOOT_BASE_URL: $springBootBaseUrl
                |SPRING_BOOT_API_KEY: ${if (springBootApiKey?.isNotBlank() == true) "[SET]" else "[NOT SET]"}
                |DATABASE_MODE: $databaseMode
                |FIREBASE_PROJECT_ID: ${if (firebaseProjectId.isNotEmpty()) "[SET]" else "[NOT SET]"}
                |FIREBASE_API_KEY: ${if (firebaseApiKey.isNotEmpty()) "[SET]" else "[NOT SET]"}
                |IS_FIREBASE_CONFIGURED: $isFirebaseConfigured
                |IS_CLOUD_ENABLED: $isCloudEnabled
                |IS_LOCAL_ENABLED: $isLocalEnabled
                |DEFAULT_THEME: $defaultTheme
                |DEFAULT_LANGUAGE: $defaultLanguage
                |===========================
            """.trimMargin()
            
            Napier.d(configSummary)
            
            // Check for default values that should be overridden
            if (aiBackendType == "N8N" && (n8nBaseUrlEnv.isNullOrBlank() || n8nWebhookPathEnv.isNullOrBlank())) {
                Napier.w("WARNING: Missing N8N configuration (N8N_BASE_URL / N8N_WEBHOOK_PATH). n8n backend will not work.")
            }
        }
    }
}
