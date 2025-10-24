package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.model.Language
import app.prototype.creator.data.service.ExportFormat
import app.prototype.creator.data.service.ExportResult
import app.prototype.creator.data.service.ExportService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Export button for Android that allows users to export prototype designs
 */
@Composable
fun AndroidExportButton(
    htmlContent: String,
    prototypeName: String,
    currentLanguage: Language,
    modifier: Modifier = Modifier,
    exportService: ExportService = koinInject()
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val exportLabel = when (currentLanguage) {
        Language.SPANISH -> "Exportar"
        Language.ENGLISH -> "Export"
    }
    
    val htmlLabel = when (currentLanguage) {
        Language.SPANISH -> "Exportar como HTML"
        Language.ENGLISH -> "Export as HTML"
    }
    
    val pdfLabel = when (currentLanguage) {
        Language.SPANISH -> "Exportar como PDF"
        Language.ENGLISH -> "Export as PDF"
    }
    
    IconButton(
        onClick = { 
            Napier.d("📥 Export button clicked")
            expanded = true 
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = exportLabel
        )
    }
    
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { 
            Napier.d("📥 Dropdown dismissed")
            expanded = false 
        }
    ) {
        DropdownMenuItem(
            text = { Text(htmlLabel) },
            onClick = {
                Napier.d("📥 HTML export clicked")
                expanded = false
                scope.launch {
                    try {
                        Napier.d("📥 Starting HTML export...")
                        val result = exportService.exportPrototype(
                            htmlContent = htmlContent,
                            format = ExportFormat.HTML,
                            suggestedFileName = prototypeName
                        )
                        Napier.d("📥 HTML export result: $result")
                    } catch (e: Exception) {
                        Napier.e("❌ Error during HTML export", e)
                        e.printStackTrace()
                    }
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null
                )
            }
        )
        
        DropdownMenuItem(
            text = { Text(pdfLabel) },
            onClick = {
                Napier.d("📥 PDF export clicked")
                expanded = false
                scope.launch {
                    try {
                        Napier.d("📥 Starting PDF export...")
                        val result = exportService.exportPrototype(
                            htmlContent = htmlContent,
                            format = ExportFormat.PDF,
                            suggestedFileName = prototypeName
                        )
                        Napier.d("📥 PDF export result: $result")
                    } catch (e: Exception) {
                        Napier.e("❌ Error during PDF export", e)
                        e.printStackTrace()
                    }
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null
                )
            }
        )
    }
}

/**
 * Full export dialog for Android
 */
@Composable
fun AndroidExportDialog(
    htmlContent: String,
    prototypeName: String,
    currentLanguage: Language,
    onDismiss: () -> Unit,
    exportService: ExportService = koinInject()
) {
    var isExporting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val titleLabel = when (currentLanguage) {
        Language.SPANISH -> "Exportar Diseño"
        Language.ENGLISH -> "Export Design"
    }
    
    val descriptionLabel = when (currentLanguage) {
        Language.SPANISH -> "Selecciona el formato para exportar tu diseño de prototipo"
        Language.ENGLISH -> "Select the format to export your prototype design"
    }
    
    val htmlLabel = when (currentLanguage) {
        Language.SPANISH -> "Exportar como HTML"
        Language.ENGLISH -> "Export as HTML"
    }
    
    val pdfLabel = when (currentLanguage) {
        Language.SPANISH -> "Exportar como PDF"
        Language.ENGLISH -> "Export as PDF"
    }
    
    val cancelLabel = when (currentLanguage) {
        Language.SPANISH -> "Cancelar"
        Language.ENGLISH -> "Cancel"
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(titleLabel)
        Text(descriptionLabel)
        
        Button(
            onClick = {
                scope.launch {
                    isExporting = true
                    try {
                        exportService.exportPrototype(
                            htmlContent = htmlContent,
                            format = ExportFormat.HTML,
                            suggestedFileName = prototypeName
                        )
                    } finally {
                        isExporting = false
                        onDismiss()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isExporting
        ) {
            Text(htmlLabel)
        }
        
        Button(
            onClick = {
                scope.launch {
                    isExporting = true
                    try {
                        exportService.exportPrototype(
                            htmlContent = htmlContent,
                            format = ExportFormat.PDF,
                            suggestedFileName = prototypeName
                        )
                    } finally {
                        isExporting = false
                        onDismiss()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isExporting
        ) {
            Text(pdfLabel)
        }
        
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isExporting
        ) {
            Text(cancelLabel)
        }
    }
}
