package app.prototype.creator.data.service

import app.prototype.creator.data.model.Language
import io.github.aakira.napier.Napier

/**
 * Desktop implementation - updates the PlatformExporter companion object
 */
actual fun updateExporterLanguage(language: Language) {
    Napier.d("🌐 Desktop exporter language updated to: ${language.displayName}")
    PlatformExporter.currentLanguage = language
}
