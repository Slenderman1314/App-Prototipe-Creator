package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.model.Language
import app.prototype.creator.utils.StoragePreferences
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackendSelectorDialog(
    currentLanguage: Language,
    storagePreferences: StoragePreferences,
    onDismiss: () -> Unit,
    onBackendChanged: (String) -> Unit
) {
    var selectedBackend by remember { mutableStateOf(storagePreferences.getAiBackend()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (currentLanguage == Language.SPANISH) 
                        "Seleccionar Backend de IA" 
                    else 
                        "Select AI Backend",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (currentLanguage == Language.SPANISH)
                        "Elige el backend que procesará las conversaciones con IA:"
                    else
                        "Choose the backend that will process AI conversations:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Spring Boot Option
                BackendOption(
                    title = "Spring Boot",
                    description = if (currentLanguage == Language.SPANISH)
                        "Backend personalizado con control total"
                    else
                        "Custom backend with full control",
                    isSelected = selectedBackend == StoragePreferences.BACKEND_SPRING_BOOT,
                    onClick = { selectedBackend = StoragePreferences.BACKEND_SPRING_BOOT }
                )
                
                // n8n Option
                BackendOption(
                    title = "n8n",
                    description = if (currentLanguage == Language.SPANISH)
                        "Workflows automatizados con n8n"
                    else
                        "Automated workflows with n8n",
                    isSelected = selectedBackend == StoragePreferences.BACKEND_N8N,
                    onClick = { selectedBackend = StoragePreferences.BACKEND_N8N }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    storagePreferences.setAiBackend(selectedBackend)
                    Napier.d("🤖 Backend changed to: $selectedBackend")
                    onBackendChanged(selectedBackend)
                    onDismiss()
                }
            ) {
                Text(
                    text = if (currentLanguage == Language.SPANISH) "Aplicar" else "Apply"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (currentLanguage == Language.SPANISH) "Cancelar" else "Cancel"
                )
            }
        }
    )
}

@Composable
private fun BackendOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
