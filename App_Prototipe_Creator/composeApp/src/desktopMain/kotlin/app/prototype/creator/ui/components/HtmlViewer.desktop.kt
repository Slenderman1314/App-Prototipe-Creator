package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import java.awt.BorderLayout
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.text.html.HTMLEditorKit

@Composable
actual fun HtmlViewer(
    htmlContent: String,
    modifier: Modifier
) {
    Napier.d("🌐 HtmlViewer rendering HTML (length: ${htmlContent.length})")
    
    SwingPanel(
        modifier = modifier,
        factory = {
            JPanel(BorderLayout()).apply {
                val editorPane = JEditorPane().apply {
                    contentType = "text/html"
                    editorKit = HTMLEditorKit()
                    isEditable = false
                    text = htmlContent
                }
                val scrollPane = JScrollPane(editorPane)
                add(scrollPane, BorderLayout.CENTER)
            }
        },
        update = { panel ->
            try {
                val scrollPane = panel.getComponent(0) as JScrollPane
                val editorPane = scrollPane.viewport.view as JEditorPane
                editorPane.text = htmlContent
                Napier.d("✅ HTML updated in viewer")
            } catch (e: Exception) {
                Napier.e("❌ Error updating HTML viewer", e)
            }
        }
    )
}
