package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

// Track open windows to prevent duplicates
private val openWindows = mutableMapOf<String, JFrame>()

/**
 * Desktop implementation using JavaFX WebView in a separate window
 * This avoids SwingPanel event blocking issues
 */
@Composable
actual fun HtmlViewer(
    htmlContent: String,
    modifier: Modifier,
    key: Any?
) {
    val windowKey = key.toString()
    Napier.d("🌐 HtmlViewer (Desktop/JavaFX Window) rendering HTML (length: ${htmlContent.length}) for key: $windowKey")
    
    // Open window whenever key changes
    LaunchedEffect(key) {
        Napier.d("🪟 LaunchedEffect triggered for key: $windowKey")
        
        // Close existing window with same key if it exists
        openWindows[windowKey]?.let { existingFrame ->
            Napier.d("🗑️ Closing existing window for key: $windowKey")
            SwingUtilities.invokeLater {
                existingFrame.dispose()
            }
            openWindows.remove(windowKey)
        }
        
        // Open new window
        Napier.d("🪟 Opening new JavaFX WebView window for key: $windowKey")
        openHtmlInWindow(htmlContent, windowKey)
    }
    
    // Cleanup when composable leaves composition
    // NOTE: We don't close the window here because we want it to stay open
    // even after navigating away from the detail screen
    DisposableEffect(key) {
        onDispose {
            Napier.d("🧹 Disposing HtmlViewer for key: $windowKey (window stays open)")
            // Don't close the window - let the user close it manually
        }
    }
    
    // Show nothing - the window is separate
    // Don't render anything to avoid blocking UI interactions
    Box(modifier = modifier.size(0.dp))
}

private fun openHtmlInWindow(htmlContent: String, windowKey: String) {
    SwingUtilities.invokeLater {
        try {
            Napier.d("🪟 Creating JFrame for HTML window with key: $windowKey")
            Napier.d("📝 HTML content length: ${htmlContent.length}")
            Napier.d("📝 HTML preview: ${htmlContent.take(200)}")
            val frame = JFrame("Prototipo: $windowKey")
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame.size = Dimension(1200, 800)
            frame.setLocationRelativeTo(null)
            
            // Add window listener to remove from tracking when closed
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    Napier.d("🚪 Window closed for key: $windowKey")
                    openWindows.remove(windowKey)
                }
            })
            
            val panel = JPanel(BorderLayout())
            val jfxPanel = JFXPanel() // This initializes JavaFX toolkit
            panel.add(jfxPanel, BorderLayout.CENTER)
            frame.contentPane = panel
            
            // Make frame visible first
            frame.isVisible = true
            
            // Wait for JavaFX to be ready, then create WebView
            Platform.runLater {
                try {
                    Napier.d("🌐 Creating JavaFX WebView in window")
                    val webView = WebView()
                    val webEngine = webView.engine
                    
                    webEngine.loadWorker.stateProperty().addListener { _, _, newState ->
                        Napier.d("📊 WebView load state: $newState")
                    }
                    
                    // Load HTML content
                    Napier.d("📄 Loading HTML content into WebEngine")
                    webEngine.loadContent(htmlContent, "text/html")
                    
                    val scene = Scene(webView)
                    jfxPanel.scene = scene
                    
                    Napier.d("✅ JavaFX WebView loaded in window")
                } catch (e: Exception) {
                    Napier.e("❌ Error creating WebView in window", e)
                    e.printStackTrace()
                }
            }
            
            // Register window
            openWindows[windowKey] = frame
            Napier.d("✅ HTML window opened successfully and registered with key: $windowKey")
        } catch (e: Exception) {
            Napier.e("❌ Error opening HTML window", e)
        }
    }
}

