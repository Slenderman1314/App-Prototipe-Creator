package app.prototype.creator.data.service

import io.github.aakira.napier.Napier

/**
 * Export formats supported by the application
 */
enum class ExportFormat(val extension: String, val mimeType: String, val displayName: String) {
    HTML("html", "text/html", "HTML"),
    PDF("pdf", "application/pdf", "PDF")
}

/**
 * Result of an export operation
 */
sealed class ExportResult {
    data class Success(val filePath: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
    object Cancelled : ExportResult()
}

/**
 * Platform-specific export service interface
 */
interface ExportService {
    /**
     * Export HTML content to a file
     * @param htmlContent The HTML content to export
     * @param format The export format
     * @param suggestedFileName Suggested file name (without extension)
     * @return ExportResult indicating success, error, or cancellation
     */
    suspend fun exportPrototype(
        htmlContent: String,
        format: ExportFormat,
        suggestedFileName: String
    ): ExportResult
}

/**
 * Common export service implementation
 */
class CommonExportService(
    private val platformExporter: PlatformExporter
) : ExportService {
    
    /**
     * Set the current language for the exporter
     */
    fun setLanguage(language: app.prototype.creator.data.model.Language) {
        // Platform-specific language setting will be handled in platform implementations
        Napier.d("🌐 Export service language set to: ${language.displayName}")
    }
    
    override suspend fun exportPrototype(
        htmlContent: String,
        format: ExportFormat,
        suggestedFileName: String
    ): ExportResult {
        Napier.d("📤 Exporting prototype as ${format.name}: $suggestedFileName")
        
        return try {
            when (format) {
                ExportFormat.HTML -> platformExporter.exportAsHtml(htmlContent, suggestedFileName)
                ExportFormat.PDF -> platformExporter.exportAsPdf(htmlContent, suggestedFileName)
            }
        } catch (e: Exception) {
            Napier.e("❌ Error exporting prototype", e)
            ExportResult.Error(e.message ?: "Unknown error occurred")
        }
    }
}

/**
 * Platform-specific exporter interface
 */
expect class PlatformExporter() {
    suspend fun exportAsHtml(htmlContent: String, suggestedFileName: String): ExportResult
    suspend fun exportAsPdf(htmlContent: String, suggestedFileName: String): ExportResult
}
