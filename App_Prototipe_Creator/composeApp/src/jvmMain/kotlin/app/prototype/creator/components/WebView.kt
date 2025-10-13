package app.prototype.creator.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import java.awt.Color
import java.awt.BorderLayout
import java.net.URL
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.event.HyperlinkEvent

@Composable
actual fun WebView(
    url: String,
    modifier: Modifier,
    onLoadingStateChanged: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    // Create a SwingPanel to host the Swing components
    SwingPanel(
        background = androidx.compose.ui.graphics.Color.White,
        modifier = modifier,
        factory = {
            val panel = JPanel(BorderLayout())
            val editorPane = JEditorPane().apply {
                isEditable = false
                contentType = "text/html"
                
                // Handle hyperlink clicks
                addHyperlinkListener { event ->
                    if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            // Try to open the URL in the default browser
                            java.awt.Desktop.getDesktop().browse(event.url.toURI())
                        } catch (e: Exception) {
                            onError("Failed to open URL: ${e.message}")
                        }
                    }
                }
            }
            
            // Add the editor pane to a scroll pane
            val scrollPane = JScrollPane(editorPane)
            panel.add(scrollPane, BorderLayout.CENTER)
            
            // Load the URL in a background thread
            Thread {
                try {
                    onLoadingStateChanged(true)
                    
                    // Load the URL content
                    val urlObj = URL(url)
                    val content = urlObj.readText()
                    
                    // Update the UI on the EDT
                    SwingUtilities.invokeLater {
                        editorPane.text = """
                            <html>
                                <head>
                                    <style>
                                        body { font-family: Arial, sans-serif; padding: 20px; }
                                        a { color: #0066cc; text-decoration: none; }
                                        a:hover { text-decoration: underline; }
                                    </style>
                                </head>
                                <body>
                                    <h2>Web Content</h2>
                                    <p>Loading content from: $url</p>
                                    <div>$content</div>
                                </body>
                            </html>
                        """.trimIndent()
                        onLoadingStateChanged(false)
                    }
                } catch (e: Exception) {
                    // Update the UI on the EDT in case of error
                    SwingUtilities.invokeLater {
                        editorPane.text = """
                            <html>
                                <body>
                                    <h2>Error Loading Content</h2>
                                    <p>Could not load content from: $url</p>
                                    <p>Error: ${e.message}</p>
                                </body>
                            </html>
                        """.trimIndent()
                        onLoadingStateChanged(false)
                        onError("Failed to load URL: ${e.message}")
                    }
                }
            }.start()
            
            panel
        }
    )
}
