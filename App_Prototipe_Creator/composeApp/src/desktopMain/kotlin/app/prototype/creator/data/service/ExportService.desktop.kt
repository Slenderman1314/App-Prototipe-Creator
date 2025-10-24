package app.prototype.creator.data.service

import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.model.Language
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.html2pdf.ConverterProperties
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.CompletableDeferred
import java.awt.FileDialog
import java.awt.Frame

/**
 * Desktop implementation of the platform exporter
 */
actual class PlatformExporter {
    companion object {
        var currentLanguage: Language = Language.SPANISH
    }
    
    actual suspend fun exportAsHtml(htmlContent: String, suggestedFileName: String): ExportResult {
        return withContext(Dispatchers.IO) {
            try {
                Napier.d("📝 Exporting as HTML: $suggestedFileName")
                
                // Show file chooser dialog
                val file = showSaveDialog(suggestedFileName, "html", "HTML Files")
                
                if (file != null) {
                    // Write HTML content to file
                    file.writeText(htmlContent)
                    Napier.d("✅ HTML exported successfully to: ${file.absolutePath}")
                    ExportResult.Success(file.absolutePath)
                } else {
                    Napier.d("ℹ️ HTML export cancelled by user")
                    ExportResult.Cancelled
                }
            } catch (e: Exception) {
                Napier.e("❌ Error exporting HTML", e)
                ExportResult.Error(e.message ?: "Failed to export HTML")
            }
        }
    }
    
    actual suspend fun exportAsPdf(htmlContent: String, suggestedFileName: String): ExportResult {
        return withContext(Dispatchers.IO) {
            try {
                Napier.d("📄 Exporting as PDF: $suggestedFileName")
                
                // Show file chooser dialog
                val file = showSaveDialog(suggestedFileName, "pdf", "PDF Files")
                
                if (file != null) {
                    // Use JavaFX WebView to render HTML and print to PDF
                    val result = printHtmlToPdf(htmlContent, file)
                    
                    if (result) {
                        Napier.d("✅ PDF exported successfully to: ${file.absolutePath}")
                        ExportResult.Success(file.absolutePath)
                    } else {
                        Napier.e("❌ Failed to generate PDF")
                        ExportResult.Error("Failed to generate PDF")
                    }
                } else {
                    Napier.d("ℹ️ PDF export cancelled by user")
                    ExportResult.Cancelled
                }
            } catch (e: Exception) {
                Napier.e("❌ Error exporting PDF", e)
                ExportResult.Error(e.message ?: "Failed to export PDF")
            }
        }
    }
    
    /**
     * Shows a save file dialog using JFileChooser (cross-platform)
     */
    private suspend fun showSaveDialog(
        suggestedFileName: String,
        extension: String,
        description: String
    ): File? {
        val deferred = CompletableDeferred<File?>()
        
        SwingUtilities.invokeLater {
            try {
                Napier.d("🗂️ Showing save dialog for: $suggestedFileName.$extension")
                
                val fileChooser = JFileChooser()
                fileChooser.dialogTitle = Strings.saveFile.localized(PlatformExporter.currentLanguage)
                fileChooser.selectedFile = File("$suggestedFileName.$extension")
                
                // Use localized file type description
                val localizedDescription = when (extension) {
                    "html" -> Strings.htmlFiles.localized(PlatformExporter.currentLanguage)
                    "pdf" -> Strings.pdfFiles.localized(PlatformExporter.currentLanguage)
                    else -> description
                }
                fileChooser.fileFilter = FileNameExtensionFilter(localizedDescription, extension)
                
                val result = fileChooser.showSaveDialog(null)
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    var selectedFile = fileChooser.selectedFile
                    
                    // Ensure the file has the correct extension
                    if (!selectedFile.name.endsWith(".$extension")) {
                        selectedFile = File(selectedFile.absolutePath + ".$extension")
                    }
                    
                    Napier.d("✅ File selected: ${selectedFile.absolutePath}")
                    deferred.complete(selectedFile)
                } else {
                    Napier.d("❌ No file selected")
                    deferred.complete(null)
                }
            } catch (e: Exception) {
                Napier.e("❌ Error showing save dialog", e)
                deferred.completeExceptionally(e)
            }
        }
        
        return deferred.await()
    }
    
    /**
     * Converts HTML content to PDF using iText 7 html2pdf library
     * This library has much better support for modern HTML5 and CSS3
     */
    private suspend fun printHtmlToPdf(htmlContent: String, outputFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Napier.d("🖨️ Converting HTML to PDF using iText html2pdf")
                Napier.d("📏 Original HTML length: ${htmlContent.length}")
                
                // Prepare HTML for PDF conversion (minimal processing needed with iText)
                val processedHtml = prepareHtmlForPdf(htmlContent)
                Napier.d("📏 Processed HTML length: ${processedHtml.length}")
                
                // Debug: Save processed HTML to temp file for inspection
                try {
                    val tempFile = File.createTempFile("pdf_debug_", ".html")
                    tempFile.writeText(processedHtml)
                    Napier.d("🔍 Debug HTML saved to: ${tempFile.absolutePath}")
                } catch (e: Exception) {
                    Napier.w("⚠️ Could not save debug file: ${e.message}")
                }
                
                // Configure converter properties
                val converterProperties = ConverterProperties()
                
                // Create PDF from HTML using iText
                FileOutputStream(outputFile).use { os ->
                    try {
                        HtmlConverter.convertToPdf(processedHtml, os, converterProperties)
                        Napier.d("✅ PDF generated successfully with iText")
                    } catch (e: Exception) {
                        Napier.e("❌ HtmlConverter.convertToPdf() failed", e)
                        Napier.e("Error details: ${e.message}")
                        e.printStackTrace()
                        throw e
                    }
                }
                
                true
            } catch (e: Exception) {
                Napier.e("❌ Error converting HTML to PDF: ${e.javaClass.simpleName}", e)
                Napier.e("Error message: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }
    
    /**
     * Prepares HTML for PDF conversion with iText
     * iText has much better CSS support, so minimal processing is needed
     */
    private fun prepareHtmlForPdf(htmlContent: String): String {
        var html = htmlContent
        
        // Remove JavaScript (not supported in PDF)
        html = html.replace(Regex("<script[^>]*>.*?</script>", RegexOption.DOT_MATCHES_ALL), "")
        html = html.replace(Regex("<script[^>]*/>"), "")
        
        // Make all screens visible for PDF
        html = html.replace(Regex("display:\\s*none"), "display: block")
        html = html.replace(Regex("visibility:\\s*hidden"), "visibility: visible")
        
        // Add enhanced PDF-specific styles for better visual presentation
        val pdfStyles = """
            <style type="text/css">
                @page {
                    size: A4;
                    margin: 1.5cm;
                }
                
                /* Force all screens to be visible */
                .screen {
                    display: block !important;
                    visibility: visible !important;
                    page-break-before: always;
                    page-break-inside: avoid;
                }
                
                .screen:first-of-type {
                    page-break-before: auto;
                }
                
                /* Prevent page breaks inside components */
                .component, .nav-buttons, .app-header {
                    page-break-inside: avoid;
                }
                
                /* Enhanced button styling for PDF */
                button, .nav-buttons button {
                    display: inline-block !important;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                    color: white !important;
                    padding: 15px 25px !important;
                    border-radius: 8px !important;
                    font-weight: 600 !important;
                    font-size: 1em !important;
                    text-align: center !important;
                    border: none !important;
                    box-shadow: 0 4px 6px rgba(0,0,0,0.1) !important;
                    margin: 5px !important;
                }
                
                /* Note about interactivity */
                button::after {
                    content: " 📄";
                    font-size: 0.8em;
                }
                
                /* Improve overall typography */
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif !important;
                    line-height: 1.6 !important;
                }
                
                /* Better spacing */
                .app-container {
                    padding: 20px !important;
                }
                
                /* Enhance headers */
                .app-header {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                    padding: 30px !important;
                    border-radius: 12px !important;
                    margin-bottom: 30px !important;
                    box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3) !important;
                }
                
                .app-title {
                    color: white !important;
                    font-size: 2.5em !important;
                    font-weight: bold !important;
                    text-align: center !important;
                    margin: 0 !important;
                    text-shadow: 2px 2px 4px rgba(0,0,0,0.2) !important;
                }
                
                .app-description {
                    color: white !important;
                    text-align: center !important;
                    font-size: 1.1em !important;
                    margin-top: 10px !important;
                }
                
                /* Screen styling */
                .screen {
                    background: white !important;
                    border: 2px solid #667eea !important;
                    border-radius: 12px !important;
                    padding: 30px !important;
                    margin: 20px 0 !important;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
                }
                
                .screen-title {
                    color: #667eea !important;
                    font-size: 2em !important;
                    font-weight: bold !important;
                    border-bottom: 3px solid #667eea !important;
                    padding-bottom: 15px !important;
                    margin-bottom: 20px !important;
                }
                
                /* Component styling */
                .component {
                    background: #f8f9fa !important;
                    border-left: 5px solid #667eea !important;
                    padding: 20px !important;
                    margin: 15px 0 !important;
                    border-radius: 6px !important;
                }
                
                .component-label {
                    color: #667eea !important;
                    font-weight: bold !important;
                    font-size: 1.2em !important;
                    margin-bottom: 10px !important;
                }
                
                /* Add watermark/note about PDF format */
                @page {
                    @bottom-right {
                        content: "📄 Documento estático - Para versión interactiva, use el archivo HTML";
                        font-size: 8pt;
                        color: #999;
                    }
                }
            </style>
        """.trimIndent()
        
        // Insert PDF styles before </head> or at the beginning if no head
        html = when {
            html.contains("</head>", ignoreCase = true) -> {
                html.replace("</head>", "$pdfStyles\n</head>", ignoreCase = true)
            }
            html.contains("<head>", ignoreCase = true) -> {
                html.replace("<head>", "<head>\n$pdfStyles", ignoreCase = true)
            }
            else -> {
                "<html><head>$pdfStyles</head><body>$html</body></html>"
            }
        }
        
        return html
    }
    
}
