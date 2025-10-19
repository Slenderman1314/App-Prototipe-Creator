package app.prototype.creator.utils

import android.content.Context
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Android implementation of getEnvironmentProperty
 * Reads from system properties (set in MainActivity from gradle.properties via BuildConfig)
 */
actual fun getEnvironmentProperty(key: String): String? {
    // Read from system properties set by MainActivity
    val fromProps = System.getProperty(key)
    if (!fromProps.isNullOrBlank()) return fromProps
    
    // Fallback to environment variables
    return System.getenv(key)
}

/**
 * Load environment variables from gradle.properties
 * These are passed via BuildConfig or can be read from resources
 */
fun loadEnvFromGradleProperties(context: Context) {
    try {
        println("📄 Loading environment variables from gradle.properties")
        
        // Try to read from raw resources (if we add gradle.properties there)
        // For now, we'll read from a properties file in assets
        val inputStream = context.assets.open("env.properties")
        val reader = BufferedReader(InputStreamReader(inputStream))
        
        var loadedCount = 0
        reader.useLines { lines ->
            lines.forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                    val parts = trimmedLine.split("=", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim()
                        System.setProperty(key, value)
                        loadedCount++
                        println("✓ Loaded: $key")
                    }
                }
            }
        }
        
        println("✅ Environment variables loaded successfully ($loadedCount properties)")
    } catch (e: FileNotFoundException) {
        println("⚠️ env.properties not found, using hardcoded values")
    } catch (e: Exception) {
        println("❌ Error loading environment variables: ${e.message}")
        e.printStackTrace()
    }
}
