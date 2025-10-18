package app.prototype.creator.utils

/**
 * Desktop implementation: Get property from System properties
 */
actual fun getEnvironmentProperty(key: String): String? {
    return System.getProperty(key)
}
