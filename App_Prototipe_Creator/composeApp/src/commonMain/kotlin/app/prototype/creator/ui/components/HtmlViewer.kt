package app.prototype.creator.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prototype.creator.data.model.Language
import app.prototype.creator.data.service.ExportService

/**
 * Platform-specific HTML viewer component
 * @param key Unique key to force recreation when changed
 * @param prototypeName Name of the prototype for export
 * @param exportService Service for exporting prototypes
 * @param language Current language for localization
 */
@Composable
expect fun HtmlViewer(
    htmlContent: String,
    modifier: Modifier,
    key: Any?,
    prototypeName: String,
    exportService: ExportService?,
    language: Language
)

/**
 * Update the language of the HtmlViewer dynamically
 * @param language New language to apply
 */
expect fun updateWebViewLanguage(language: Language)
