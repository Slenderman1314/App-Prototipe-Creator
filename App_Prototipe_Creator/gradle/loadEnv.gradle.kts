import java.io.File
import java.util.Properties

fun loadEnv(file: File) {
    if (!file.exists()) {
        logger.warn("⚠️  .env file not found at ${file.absolutePath}")
        return
    }

    val env = Properties()
    file.inputStream().use { env.load(it) }
    
    env.forEach { (key, value) ->
        if (key != null && value != null) {
            val envKey = key.toString()
            val envValue = value.toString()
            
            // Set environment variable if not already set
            if (System.getenv(envKey) == null) {
                System.setProperty(envKey, envValue)
                logger.info("✅ Loaded $envKey from .env")
            } else {
                logger.info("ℹ️  Using existing environment variable: $envKey")
            }
        }
    }
}

// Load .env file from project root
val envFile = rootProject.file(".env")
loadEnv(envFile)
