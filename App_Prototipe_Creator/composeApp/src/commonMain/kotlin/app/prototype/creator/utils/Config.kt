package app.prototype.creator.utils

import io.github.aakira.napier.Napier

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
    
    // n8n configuration
    val n8nBaseUrl: String = "https://api.example.com/n8n"
    val n8nWebhookPath: String = "webhook/chat"
    val n8nApiKey: String = ""
    
    // Theme and language configuration
    val defaultTheme: String = "system"
    val defaultLanguage: String = "es"
    
    // Complete URLs for n8n endpoints
    val n8nChatEndpoint: String
        get() = "${n8nBaseUrl.trimEnd('/')}/${n8nWebhookPath.trimStart('/')}"
    
    init {
        // Log configuration summary in debug mode
        if (DEBUG) {
            val configSummary = """
                |=== Configuration Summary ===
                |DEBUG: $DEBUG
                |API_URL: $API_URL
                |API_TIMEOUT: $API_TIMEOUT ms
                |N8N_BASE_URL: $n8nBaseUrl
                |N8N_WEBHOOK_PATH: $n8nWebhookPath
                |N8N_API_KEY: ${if (n8nApiKey.isNotBlank()) "[SET]" else "[MISSING]"}
                |DEFAULT_THEME: $defaultTheme
                |DEFAULT_LANGUAGE: $defaultLanguage
                |===========================
            """.trimMargin()
            
            Napier.d(configSummary)
            
            // Check for default values that should be overridden
            if (n8nBaseUrl == "https://api.example.com/n8n") {
                Napier.w("WARNING: Using default n8n URL. Please configure N8N_BASE_URL in code.")
            }
        }
    }
}
