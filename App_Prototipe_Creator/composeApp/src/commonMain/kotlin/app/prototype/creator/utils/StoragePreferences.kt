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
        
        const val MODE_LOCAL = "LOCAL"
        const val MODE_CLOUD = "CLOUD"
        const val MODE_HYBRID = "HYBRID"
        
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
        Napier.d("💾 Storage mode set to: $mode for user: $user")
    }
    
    /**
     * Check if storage has been configured for the current user
     */
    fun isStorageConfigured(userId: String? = null): Boolean {
        val user = userId ?: getCurrentUserId()
        val key = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_CONFIGURED}"
        val configured = settings.getBoolean(key, false)
        Napier.d("🔍 Storage configured for user $user: $configured")
        return configured
    }
    
    /**
     * Mark that storage has been configured for the current user
     */
    fun setStorageConfigured(userId: String? = null) {
        val user = userId ?: getCurrentUserId()
        val key = "${KEY_USER_PREFIX}${user}_${KEY_STORAGE_CONFIGURED}"
        settings.putBoolean(key, true)
        Napier.d("✅ Storage marked as configured for user: $user")
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
        Napier.d("🔄 Storage configuration reset for user: $user")
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
}
