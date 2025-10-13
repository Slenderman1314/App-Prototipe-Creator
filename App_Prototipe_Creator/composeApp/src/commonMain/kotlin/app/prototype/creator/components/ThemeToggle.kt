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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.prototype.creator.LocalAppSettings

@Composable
fun ThemeToggle(
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onBackground
) {
    val appSettings = LocalAppSettings.current
    
    Row(modifier = modifier) {
        IconButton(
            onClick = { appSettings.isDarkTheme = false },
            enabled = appSettings.isDarkTheme
        ) {
            Icon(
                imageVector = Icons.Default.LightMode,
                contentDescription = "Light theme",
                tint = if (!appSettings.isDarkTheme) MaterialTheme.colorScheme.primary else iconTint.copy(alpha = 0.5f)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        IconButton(
            onClick = { appSettings.isDarkTheme = true },
            enabled = !appSettings.isDarkTheme
        ) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = "Dark theme",
                tint = if (appSettings.isDarkTheme) MaterialTheme.colorScheme.primary else iconTint.copy(alpha = 0.5f)
            )
        }
    }
}
