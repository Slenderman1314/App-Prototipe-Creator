package app.prototype.creator.data.service

import app.prototype.creator.data.model.Language

/**
 * Update the exporter language for platform-specific implementations
 * This is a common function that delegates to platform-specific implementations
 */
expect fun updateExporterLanguage(language: Language)
