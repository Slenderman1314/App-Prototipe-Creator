package app.prototype.creator.data.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Android implementation of PlatformExporter
 * Handles exporting prototypes as HTML and PDF files
 */
actual class PlatformExporter {
    companion object {
        var context: Context? = null
    }
    
    /**
     * Export HTML content to a file
     */
    actual suspend fun exportAsHtml(
        htmlContent: String,
        suggestedFileName: String
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            showToast("Exportando HTML...")
            
            val fileName = sanitizeFileName(suggestedFileName)
            val file = createFile(fileName, "html")
            
            file.writeText(htmlContent, Charsets.UTF_8)
            
            Napier.d("✅ HTML exported successfully to: ${file.absolutePath}")
            
            showToast("✅ HTML guardado en:\nDescargas/${file.name}")
            
            // Optionally open share dialog after a delay
            // shareFile(file, "text/html")
            
            ExportResult.Success(file.absolutePath)
        } catch (e: Exception) {
            Napier.e("❌ Error exporting HTML", e)
            showToast("❌ Error al exportar HTML: ${e.message}")
            ExportResult.Error(e.message ?: "Error exporting HTML")
        }
    }
    
    /**
     * Export as PDF using iText library to convert HTML to PDF
     */
    actual suspend fun exportAsPdf(
        htmlContent: String,
        suggestedFileName: String
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            showToast("Generando PDF...")
            
            val fileName = sanitizeFileName(suggestedFileName)
            val file = createFile(fileName, "pdf")
            
            Napier.d("📄 Starting PDF generation for: ${file.absolutePath}")
            
            // Generate PDF from HTML using iText
            FileOutputStream(file).use { outputStream ->
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(pdfWriter)
                
                // Convert HTML to PDF
                HtmlConverter.convertToPdf(
                    ByteArrayInputStream(htmlContent.toByteArray(Charsets.UTF_8)),
                    pdfDocument
                )
                
                pdfDocument.close()
            }
            
            Napier.d("✅ PDF exported successfully to: ${file.absolutePath}")
            
            showToast("✅ PDF guardado en:\nDescargas/${file.name}")
            
            // Optionally open share dialog after a delay
            // shareFile(file, "application/pdf")
            
            ExportResult.Success(file.absolutePath)
        } catch (e: Exception) {
            Napier.e("❌ Error exporting PDF", e)
            showToast("❌ Error al generar PDF: ${e.message}")
            ExportResult.Error(e.message ?: "Error exporting PDF: ${e.localizedMessage}")
        }
    }
    
    /**
     * Create a file in the Downloads directory
     */
    private fun createFile(fileName: String, extension: String): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val finalFileName = "${fileName}_$timestamp.$extension"
        
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        return File(downloadsDir, finalFileName)
    }
    
    /**
     * Sanitize file name to remove invalid characters
     */
    private fun sanitizeFileName(fileName: String): String {
        return fileName
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
            .take(50) // Limit length
    }
    
    /**
     * Show a toast message on the main thread
     */
    private fun showToast(message: String) {
        val ctx = context ?: return
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Share the exported file
     */
    private fun shareFile(file: File, mimeType: String) {
        try {
            val ctx = context ?: run {
                Napier.e("⚠️ Context is null, trying to get from global holder")
                // Try to get context from global holder
                try {
                    val getContextFn = Class.forName("app.prototype.creator.AndroidInitializerKt")
                        .getDeclaredMethod("getApplicationContext")
                    getContextFn.invoke(null) as? android.content.Context
                } catch (e: Exception) {
                    Napier.e("❌ Failed to get context from global holder", e)
                    null
                }
            } ?: run {
                Napier.e("❌ No context available for sharing file")
                return
            }
            
            val uri: Uri = FileProvider.getUriForFile(
                ctx,
                "${ctx.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = mimeType
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            val chooser = Intent.createChooser(shareIntent, "Share prototype design").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            ctx.startActivity(chooser)
            
            Napier.d("📤 File shared successfully: ${file.name}")
        } catch (e: Exception) {
            Napier.e("⚠️ Error sharing file: ${e.message}", e)
        }
    }
}
