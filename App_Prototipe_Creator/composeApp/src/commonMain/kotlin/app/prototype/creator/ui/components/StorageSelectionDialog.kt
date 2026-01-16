package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.model.Language
import app.prototype.creator.data.repository.LanguageRepository
import app.prototype.creator.utils.Config
import org.koin.compose.koinInject

/**
 * Dialog for selecting storage mode (Local, Cloud, or Hybrid)
 */
@Composable
fun StorageSelectionDialog(
    onModeSelected: (String) -> Unit,
    onDismiss: () -> Unit = {},
    languageRepository: LanguageRepository = koinInject()
) {
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    var selectedMode by remember { mutableStateOf("LOCAL") }
    
    AlertDialog(
        onDismissRequest = { /* No permitir cerrar sin seleccionar */ },
        icon = {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = "Storage",
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = Strings.storageSelection.localized(currentLanguage),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Selector at the top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactLanguageSelector()
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = Strings.storageSelectionDescription.localized(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // LOCAL Option
                StorageOption(
                    icon = Icons.Default.CloudOff,
                    title = Strings.storageLocal.localized(currentLanguage),
                    description = Strings.storageLocalDescription.localized(currentLanguage),
                    isSelected = selectedMode == "LOCAL",
                    isAvailable = true,
                    onClick = { selectedMode = "LOCAL" }
                )
                
                // CLOUD Option
                StorageOption(
                    icon = Icons.Default.Cloud,
                    title = Strings.storageCloud.localized(currentLanguage),
                    description = Strings.storageCloudDescription.localized(currentLanguage),
                    isSelected = selectedMode == "CLOUD",
                    isAvailable = Config.isFirebaseConfigured,
                    onClick = { selectedMode = "CLOUD" },
                    warningText = if (!Config.isFirebaseConfigured) {
                        Strings.firebaseNotConfigured.localized(currentLanguage)
                    } else null
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onModeSelected(selectedMode) },
                enabled = (selectedMode == "LOCAL") || 
                         (selectedMode == "CLOUD" && Config.isFirebaseConfigured)
            ) {
                Text(Strings.continueButton.localized(currentLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.cancel.localized(currentLanguage))
            }
        }
    )
}

@Composable
private fun StorageOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
    warningText: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                enabled = isAvailable,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else if (!isAvailable) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
            )
        } else {
            CardDefaults.outlinedCardBorder()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = if (isAvailable) {
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isAvailable) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isAvailable) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    }
                )
                
                if (warningText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = warningText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            RadioButton(
                selected = isSelected,
                onClick = null,
                enabled = isAvailable
            )
        }
    }
}
