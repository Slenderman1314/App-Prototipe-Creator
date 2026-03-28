package app.prototype.creator.utils

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier

/**
 * Manages storage mode preferences and user account settings
 */
class StoragePreferences(private val settings: Settings) {
    
    companion object {
        private const val KEY_STORAGE_MODE = "storage_mode"
        private const val KEY_STORAGE_CONFIGURED = "storage_configured"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_USER_PREFIX = "user_"
        private const val KEY_AI_PROVIDER = "ai_provider"
        private const val KEY_OPENAI_API_KEY = "openai_api_key"
        private const val KEY_ANTHROPIC_API_KEY = "anthropic_api_key"
        private const val KEY_GOOGLE_API_KEY = "google_api_key"
        private const val KEY_API_CONFIGURED = "api_configured"
        
        const val MODE_LOCAL = "LOCAL"
        const val MODE_CLOUD = "CLOUD"
        const val MODE_HYBRID = "HYBRID"
        
        // AI Providers
        const val PROVIDER_OPENAI = "OPENAI"
        const val PROVIDER_ANTHROPIC = "ANTHROPIC"
        const val PROVIDER_GOOGLE = "GOOGLE"
        
        // ========== N8N PROVIDER (FOR TESTING ONLY) ==========
        // TODO: Remove n8n when no longer needed for testing
        // To remove: Delete this constant and all references in:
        // - AppModule.kt (N8nAIService instantiation)
        // - Config.kt (n8n configuration)
        // - N8nAIService.kt (entire file)
        const val PROVIDER_N8N = "N8N"
        // ====================================================
        
        // Legacy support (deprecated)
        const val BACKEND_N8N = "N8N"
        
        private const val DEFAULT_USER_ID = "default_user"
    }
    
    /**
     * Get the current user ID (for future multi-user support)
     */
    fun getCurrentUserId(): String {
        return settings.getString(KEY_CURRENT_USER_ID, DEFAULT_USER_ID)
    }
    
    /**
     * Set the current user ID
     */
    fun setCurrentUserId(userId: String) {
        settings.putString(KEY_CURRENT_USER_ID, userId)
        Napier.d("👤 Current user ID set to: $userId")
    }
    
    /**
     * Get the current storage mode for the current user
     */
    fun getStorageMode(userId: String? = null): String {
        val user = userId ?: getCurrentUserId()
        val key = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_MODE}"
        return settings.getString(key, MODE_LOCAL)
    }
    
    /**
     * Set the storage mode for the current user
     */
    fun setStorageMode(mode: String, userId: String? = null) {
        val user = userId ?: getCurrentUserId()
        val key = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_MODE}"
        settings.putString(key, mode)
        Napier.d("Storage mode set to: $mode for user: $user")
    }
    
    /**
     * Check if storage has been configured for the current user
     */
    fun isStorageConfigured(userId: String? = null): Boolean {
        val user = userId ?: getCurrentUserId()
        val key = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_CONFIGURED}"
        val configured = settings.getBoolean(key, false)
        Napier.d("Storage configured for user $user: $configured")
        return configured
    }
    
    /**
     * Mark that storage has been configured for the current user
     */
    fun setStorageConfigured(userId: String? = null) {
        val user = userId ?: getCurrentUserId()
        val key = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_CONFIGURED}"
        settings.putBoolean(key, true)
        Napier.d("Storage marked as configured for user: $user")
    }
    
    /**
     * Check if this is the first launch (legacy support)
     * @deprecated Use isStorageConfigured() instead
     */
    @Deprecated("Use isStorageConfigured() instead")
    fun isFirstLaunch(): Boolean {
        return !isStorageConfigured()
    }
    
    /**
     * Mark that the app has been launched (legacy support)
     * @deprecated Use setStorageConfigured() instead
     */
    @Deprecated("Use setStorageConfigured() instead")
    fun setFirstLaunchComplete() {
        setStorageConfigured()
    }
    
    /**
     * Reset storage configuration for the current user (for testing)
     */
    fun resetStorageConfiguration(userId: String? = null) {
        val user = userId ?: getCurrentUserId()
        val key = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_CONFIGURED}"
        settings.putBoolean(key, false)
        Napier.d("Storage configuration reset for user: $user")
    }
    
    /**
     * Reset to first launch state (for testing)
     */
    fun resetFirstLaunch() {
        resetStorageConfiguration()
    }
    
    /**
     * Clear all preferences for the current user
     */
    fun clearUserPreferences(userId: String? = null) {
        val user = userId ?: getCurrentUserId()
        val storageKey = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_MODE}"
        val configuredKey = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_CONFIGURED}"
        
        settings.remove(storageKey)
        settings.remove(configuredKey)
        Napier.d("🗑️ Cleared preferences for user: $user")
    }
    
    /**
     * Clear all preferences (including all users)
     */
    fun clear() {
        settings.clear()
        Napier.d("🗑️ All preferences cleared")
    }
    
    /**
     * Get the current AI provider
     */
    fun getAiProvider(): String {
        return settings.getString(KEY_AI_PROVIDER, PROVIDER_OPENAI)
    }
    
    /**
     * Set the AI provider
     */
    fun setAiProvider(provider: String) {
        settings.putString(KEY_AI_PROVIDER, provider)
        Napier.d("🤖 AI Provider set to: $provider")
    }
    
    /**
     * Get OpenAI API key
     */
    fun getOpenAiApiKey(): String? {
        return settings.getStringOrNull(KEY_OPENAI_API_KEY)
    }
    
    /**
     * Set OpenAI API key
     */
    fun setOpenAiApiKey(apiKey: String) {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        require(apiKey.startsWith("sk-") && apiKey.length > 20) {
            "Invalid OpenAI API key format. Must start with 'sk-' and be at least 20 characters"
        }
        settings.putString(KEY_OPENAI_API_KEY, apiKey)
        Napier.d("OpenAI API key configured")
    }
    
    /**
     * Get Anthropic API key
     */
    fun getAnthropicApiKey(): String? {
        return settings.getStringOrNull(KEY_ANTHROPIC_API_KEY)
    }
    
    /**
     * Set Anthropic API key
     */
    fun setAnthropicApiKey(apiKey: String) {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        require(apiKey.startsWith("sk-ant-") && apiKey.length > 20) {
            "Invalid Anthropic API key format. Must start with 'sk-ant-' and be at least 20 characters"
        }
        settings.putString(KEY_ANTHROPIC_API_KEY, apiKey)
        Napier.d("Anthropic API key configured")
    }
    
    /**
     * Get Google API key
     */
    fun getGoogleApiKey(): String? {
        return settings.getStringOrNull(KEY_GOOGLE_API_KEY)
    }
    
    /**
     * Set Google API key
     */
    fun setGoogleApiKey(apiKey: String) {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        require(apiKey.length > 20) {
            "Invalid Google API key format. Must be at least 20 characters"
        }
        settings.putString(KEY_GOOGLE_API_KEY, apiKey)
        Napier.d("Google API key configured")
    }
    
    /**
     * Check if at least one API key is configured
     */
    fun hasApiKey(): Boolean {
        return !getOpenAiApiKey().isNullOrBlank() ||
               !getAnthropicApiKey().isNullOrBlank() ||
               !getGoogleApiKey().isNullOrBlank()
    }
    
    /**
     * Check if API has been configured
     */
    fun isApiConfigured(): Boolean {
        return settings.getBoolean(KEY_API_CONFIGURED, false)
    }
    
    /**
     * Mark API as configured
     */
    fun setApiConfigured(configured: Boolean = true) {
        settings.putBoolean(KEY_API_CONFIGURED, configured)
        Napier.d("API marked as configured: $configured")
    }
    
    /**
     * Clear all API keys
     */
    fun clearApiKeys() {
        settings.remove(KEY_OPENAI_API_KEY)
        settings.remove(KEY_ANTHROPIC_API_KEY)
        settings.remove(KEY_GOOGLE_API_KEY)
        settings.putBoolean(KEY_API_CONFIGURED, false)
        Napier.d("🗑️ All API keys cleared")
    }
    
    // Legacy support for old backend system
    @Deprecated("Use getAiProvider() instead")
    fun getAiBackend(): String {
        return getAiProvider()
    }
    
    @Deprecated("Use setAiProvider() instead")
    fun setAiBackend(backend: String) {
        setAiProvider(backend)
    }
}
