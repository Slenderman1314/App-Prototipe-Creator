package app.prototype.creator.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.LocalAppSettings
import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.repository.LanguageRepository
import org.koin.compose.koinInject

@Composable
fun ThemeToggle(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val appSettings = LocalAppSettings.current
    val languageRepository = koinInject<LanguageRepository>()
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showLabel) {
            Text(
                text = if (appSettings.isDarkTheme) 
                    Strings.darkMode.localized(currentLanguage) 
                else 
                    Strings.lightMode.localized(currentLanguage),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        IconButton(onClick = {
            appSettings.isDarkTheme = !appSettings.isDarkTheme
        }) {
            Icon(
                imageVector = if (appSettings.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (appSettings.isDarkTheme) 
                    Strings.lightMode.localized(currentLanguage) 
                else 
                    Strings.darkMode.localized(currentLanguage)
            )
        }
    }
}
