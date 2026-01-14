package app.prototype.creator.utils

import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

/**
 * Utility object to load environment variables from .env file
 */
object EnvLoader {
    private val properties = Properties()
    
    init {
        loadEnvFile()
    }

    private fun loadEnvFile() {
        try {
            val envContent = loadEnvFileContent() ?: run {
                println("⚠️ .env file not found")
                return
            }

            envContent.toByteArray().inputStream().use { input ->
                properties.load(input)
            }
            
            println("✅ Loaded ${properties.size} environment variables")
            properties.forEach { (key, value) ->
                val keyStr = key?.toString() ?: "null"
                val valueStr = if (keyStr.uppercase().let { 
                    it.contains("KEY") || it.contains("SECRET") || it.contains("PASSWORD") 
                }) "***" else value?.toString() ?: "null"
                println("   - $keyStr: $valueStr")
            }
        } catch (e: Exception) {
            println("❌ Error loading .env file: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun loadEnvFileContent(): String? {
        val possiblePaths = listOf(
            ".env",
            "composeApp/.env",
            "App_Prototipe_Creator/.env"
        )

        return possiblePaths.firstNotNullOfOrNull { path ->
            runCatching {
                object {}.javaClass.classLoader.getResource(path)?.readText()
                    ?: File(path).takeIf { it.exists() }?.readText()
            }.getOrNull()
        }
    }

    /**
     * Get an environment variable or throw an exception if not found
     */
    fun getOrThrow(key: String): String {
        return properties.getProperty(key) 
            ?: System.getenv(key)
            ?: throw IllegalStateException("Environment variable $key not found")
    }

    /**
     * Get an environment variable or return a default value if not found
     */
    fun getOrElse(key: String, default: String): String {
        return properties.getProperty(key) ?: System.getenv(key) ?: default
    }
}
