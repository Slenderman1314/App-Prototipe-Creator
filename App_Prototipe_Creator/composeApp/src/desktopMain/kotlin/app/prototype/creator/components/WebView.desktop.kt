package app.prototype.creator.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI
import javax.swing.JButton
import javax.swing.JPanel
import java.awt.BorderLayout

@Composable
actual fun WebView(
    url: String,
    modifier: Modifier,
    onLoadingStateChanged: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    // For desktop, we'll show a message and a button to open in browser
    // A full WebView implementation would require JavaFX or CEF
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "WebView no disponible en Desktop",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "URL: $url",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(URI(url))
                        }
                    } catch (e: Exception) {
                        onError("Error al abrir el navegador: ${e.message}")
                    }
                }
            ) {
                Text("Abrir en navegador")
            }
        }
    }
    
    LaunchedEffect(Unit) {
        onLoadingStateChanged(false)
    }
}
