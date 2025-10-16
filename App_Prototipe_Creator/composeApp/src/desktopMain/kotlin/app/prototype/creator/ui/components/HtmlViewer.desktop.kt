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

// Public function to initialize the window manager from App.kt
fun initializeSharedWindowManager() {
    SharedWindowManager.ensureInitialized()
}

// Singleton to manage shared window - JavaFX WebView can't be reliably recreated
private object SharedWindowManager {
    var window: JFrame? = null
    var webView: WebView? = null
    private var isInitializing = false
    private val initLock = Any()
    
    init {
        // Start initialization immediately when class is loaded
        ensureInitialized()
    }
    
    fun ensureInitialized() {
        synchronized(initLock) {
            if (window == null && !isInitializing) {
                isInitializing = true
                Napier.d("🪟 Initializing shared JavaFX WebView window")
                createWindow()
            }
        }
    }
    
    private fun createWindow() {
        SwingUtilities.invokeLater {
            try {
                Napier.d("🪟 Creating shared JFrame")
                val frame = JFrame("Prototipo")
                frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE
                frame.size = Dimension(1200, 800)
                frame.setLocationRelativeTo(null)
                
                val panel = JPanel(BorderLayout())
                val jfxPanel = JFXPanel() // Initializes JavaFX toolkit
                panel.add(jfxPanel, BorderLayout.CENTER)
                frame.contentPane = panel
                
                window = frame
                Napier.d("✅ Shared JFrame created")
                
                // Create WebView on JavaFX thread
                Platform.runLater {
                    try {
                        Napier.d("🌐 Creating shared JavaFX WebView")
                        val wv = WebView()
                        Napier.d("✅ WebView created")
                        
                        wv.engine.loadWorker.stateProperty().addListener { _, _, newState ->
                            Napier.d("📊 WebView state: $newState")
                        }
                        
                        val scene = Scene(wv)
                        jfxPanel.scene = scene
                        
                        webView = wv
                        isInitializing = false
                        Napier.d("✅ Shared WebView initialized successfully")
                    } catch (e: Exception) {
                        Napier.e("❌ Error creating shared WebView", e)
                        isInitializing = false
                    }
                }
            } catch (e: Exception) {
                Napier.e("❌ Error creating shared window", e)
                isInitializing = false
            }
        }
    }
}

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
    Napier.d("🌐 HtmlViewer rendering HTML (length: ${htmlContent.length}) for key: $windowKey")
    
    LaunchedEffect(key, htmlContent) {
        Napier.d("🪟 LaunchedEffect triggered for key: $windowKey")
        
        // Ensure window is initialized
        SharedWindowManager.ensureInitialized()
        
        // Wait for window to be ready
        var retries = 0
        while (SharedWindowManager.webView == null && retries < 50) {
            kotlinx.coroutines.delay(100)
            retries++
        }
        
        if (SharedWindowManager.webView != null) {
            // Update content
            Platform.runLater {
                try {
                    Napier.d("📝 Loading HTML content for: $windowKey")
                    SharedWindowManager.webView!!.engine.loadContent(htmlContent, "text/html")
                } catch (e: Exception) {
                    Napier.e("❌ Error loading HTML", e)
                }
            }
            
            // Show and focus window
            SwingUtilities.invokeLater {
                SharedWindowManager.window?.apply {
                    title = "Prototipo: $windowKey"
                    isVisible = true
                    toFront()
                    requestFocus()
                }
            }
        } else {
            Napier.e("❌ Failed to initialize WebView after $retries retries")
        }
    }
    
    Box(modifier = modifier.size(0.dp))
}
