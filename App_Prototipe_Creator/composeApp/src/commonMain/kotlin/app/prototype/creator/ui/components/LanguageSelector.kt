package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.model.Language
import app.prototype.creator.data.repository.LanguageRepository
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject

/**
 * Language selector component that displays a dropdown menu
 * to switch between Spanish and English
 */
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    languageRepository: LanguageRepository = koinInject()
) {
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    
    Napier.d("🌐 LanguageSelector: Rendering with currentLanguage = $currentLanguage")
    
    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = when (currentLanguage) {
                    Language.SPANISH -> "Idioma"
                    Language.ENGLISH -> "Language"
                }
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Language.entries.forEach { language ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(language.nativeName)
                            if (language == currentLanguage) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    onClick = {
                        Napier.d("🌐 LanguageSelector: User clicked on $language")
                        languageRepository.setLanguage(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Compact language selector that shows only the language code
 */
@Composable
fun CompactLanguageSelector(
    modifier: Modifier = Modifier,
    languageRepository: LanguageRepository = koinInject()
) {
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        TextButton(
            onClick = { expanded = true }
        ) {
            Text(
                text = currentLanguage.code.uppercase(),
                style = MaterialTheme.typography.labelLarge
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Language.entries.forEach { language ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(language.nativeName)
                            Spacer(modifier = Modifier.weight(1f))
                            if (language == currentLanguage) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    onClick = {
                        languageRepository.setLanguage(language)
                        expanded = false
                    }
                )
            }
        }
    }
}
