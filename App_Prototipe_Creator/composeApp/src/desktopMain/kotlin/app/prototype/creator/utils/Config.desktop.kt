package app.prototype.creator.utils

/**
 * Desktop implementation of getEnvironmentProperty
 * Reads first from JVM system properties (set in Main.loadEnvFile),
 * then falls back to OS environment variables.
 */
actual fun getEnvironmentProperty(key: String): String? {
    // First, try system properties set by loadEnvFile()
    val fromProps = System.getProperty(key)
    if (!fromProps.isNullOrBlank()) return fromProps
    // Fallback to environment variables
    return System.getenv(key)
}
