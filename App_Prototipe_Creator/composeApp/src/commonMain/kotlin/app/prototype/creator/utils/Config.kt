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
    
    private fun getRequiredProperty(key: String, defaultValue: String? = null): String {
        val value = getEnvironmentProperty(key)?.takeIf { it.isNotBlank() }
        
        if (value == null) {
            val error = """
                ❌ CONFIGURATION ERROR: $key is not set or is empty.
                
                Please check your .env file at the project root.
                The .env file should contain:
                $key=your_value_here
                
                Current working directory: ${System.getProperty("user.dir")}
                
                ${if (defaultValue != null) "Using default value: $defaultValue" else "No default value available."}
            """.trimIndent()
            
            Napier.e(error)
            
            if (defaultValue != null) {
                Napier.w("⚠️ Using default value for $key: $defaultValue")
                return defaultValue
            }
            
            throw IllegalStateException(error)
        }
        
        return value
    }

    // n8n configuration with defaults
    val n8nBaseUrl: String = getRequiredProperty("N8N_BASE_URL", "https://example.com").also {
        if (it == "https://example.com") {
            Napier.w("⚠️ N8N_BASE_URL not configured, using placeholder")
        } else {
            Napier.d("ℹ️ n8n Base URL: ${it.substring(0, minOf(10, it.length))}...")
        }
    }
    
    val n8nWebhookPath: String = getRequiredProperty("N8N_WEBHOOK_PATH", "webhook/placeholder")
    val n8nApiKey: String? = getEnvironmentProperty("N8N_API_KEY")?.takeIf { it.isNotBlank() }
    
    // Supabase configuration with defaults
    val supabaseUrl: String = getRequiredProperty("SUPABASE_URL", "https://placeholder.supabase.co").also { url ->
        if (url == "https://placeholder.supabase.co") {
            Napier.w("⚠️ SUPABASE_URL not configured, using placeholder")
        } else {
            require(url.startsWith("https://")) { "Supabase URL must start with https://" }
            Napier.d("ℹ️ Supabase URL: ${url.substring(0, minOf(10, url.length))}...")
        }
    }
    
    val supabaseAnonKey: String = getRequiredProperty("SUPABASE_ANON_KEY", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.placeholder").also { key ->
        if (key == "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.placeholder") {
            Napier.w("⚠️ SUPABASE_ANON_KEY not configured, using placeholder")
        } else {
            require(key.startsWith("ey")) { "Invalid Supabase anon key format" }
            val maskedKey = if (key.length > 10) "${key.substring(0, 5)}...${key.takeLast(5)}" else "***"
            Napier.d("ℹ️ Supabase key: $maskedKey")
        }
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
