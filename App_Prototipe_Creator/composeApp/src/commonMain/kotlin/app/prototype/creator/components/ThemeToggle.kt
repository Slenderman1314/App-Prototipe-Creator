package app.prototype.creator.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.LocalAppSettings

@Composable
fun ThemeToggle(
    modifier: Modifier = Modifier
) {
    val appSettings = LocalAppSettings.current
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (appSettings.isDarkTheme) "Dark" else "Light",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Switch(
            checked = appSettings.isDarkTheme,
            onCheckedChange = { appSettings.isDarkTheme = it }
        )
    }
}
