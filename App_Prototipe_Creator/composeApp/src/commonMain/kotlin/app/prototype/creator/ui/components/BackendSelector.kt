package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var selectedProvider by remember { mutableStateOf(storagePreferences.getAiProvider()) }
    
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
                        "Seleccionar Proveedor de IA" 
                    else 
                        "Select AI Provider",
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
                        "Elige el proveedor de IA que procesará las conversaciones:"
                    else
                        "Choose the AI provider that will process conversations:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // OpenAI Option
                ProviderOption(
                    title = "OpenAI",
                    description = if (currentLanguage == Language.SPANISH)
                        "GPT-4, GPT-4o - Mejor calidad"
                    else
                        "GPT-4, GPT-4o - Best quality",
                    isSelected = selectedProvider == StoragePreferences.PROVIDER_OPENAI,
                    hasApiKey = !storagePreferences.getOpenAiApiKey().isNullOrBlank(),
                    onClick = { selectedProvider = StoragePreferences.PROVIDER_OPENAI }
                )
                
                // Anthropic Option
                ProviderOption(
                    title = "Anthropic",
                    description = if (currentLanguage == Language.SPANISH)
                        "Claude 3 - Excelente alternativa"
                    else
                        "Claude 3 - Excellent alternative",
                    isSelected = selectedProvider == StoragePreferences.PROVIDER_ANTHROPIC,
                    hasApiKey = !storagePreferences.getAnthropicApiKey().isNullOrBlank(),
                    onClick = { selectedProvider = StoragePreferences.PROVIDER_ANTHROPIC }
                )
                
                // Google Option
                ProviderOption(
                    title = "Google",
                    description = if (currentLanguage == Language.SPANISH)
                        "Gemini - Gratis con límites"
                    else
                        "Gemini - Free with limits",
                    isSelected = selectedProvider == StoragePreferences.PROVIDER_GOOGLE,
                    hasApiKey = !storagePreferences.getGoogleApiKey().isNullOrBlank(),
                    onClick = { selectedProvider = StoragePreferences.PROVIDER_GOOGLE }
                )
                
                // n8n Option
                ProviderOption(
                    title = "n8n",
                    description = if (currentLanguage == Language.SPANISH)
                        "Workflows personalizados"
                    else
                        "Custom workflows",
                    isSelected = selectedProvider == StoragePreferences.PROVIDER_N8N,
                    hasApiKey = true,
                    onClick = { selectedProvider = StoragePreferences.PROVIDER_N8N }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    storagePreferences.setAiProvider(selectedProvider)
                    Napier.d("Provider changed to: $selectedProvider")
                    onBackendChanged(selectedProvider)
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
private fun ProviderOption(
    title: String,
    description: String,
    isSelected: Boolean,
    hasApiKey: Boolean,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // API Key indicator
                    if (hasApiKey) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
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
