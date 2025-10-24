package app.prototype.creator.data.service

import app.prototype.creator.data.model.Language
import io.github.aakira.napier.Napier

/**
 * Android implementation - currently no-op as Android uses Toast messages
 * which are already localized in the PlatformExporter
 */
actual fun updateExporterLanguage(language: Language) {
    Napier.d("🌐 Android exporter language updated to: ${language.displayName}")
    // Android Toast messages are already using the language parameter
    // No need to update a global state
}
