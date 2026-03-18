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
    // Try multiple file names
    val possibleFiles = listOf("env.properties", ".env", "config.properties")
    var loaded = false
    
    for (fileName in possibleFiles) {
        try {
            println("📄 Trying to load environment variables from $fileName")
            
            val inputStream = context.assets.open(fileName)
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
            
            println("✅ Environment variables loaded successfully from $fileName ($loadedCount properties)")
            loaded = true
            break
        } catch (e: FileNotFoundException) {
            println("⚠️ $fileName not found, trying next option...")
        } catch (e: Exception) {
            println("❌ Error loading $fileName: ${e.message}")
        }
    }
    
    if (!loaded) {
        println("⚠️ No environment properties file found. Please create env.properties in assets/ with your configuration.")
        println("⚠️ The app will continue but may fail when trying to connect to services.")
    }
}
