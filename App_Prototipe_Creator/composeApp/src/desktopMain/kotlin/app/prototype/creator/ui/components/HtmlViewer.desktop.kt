package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.LocalAppSettings
import io.github.aakira.napier.Napier
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import java.awt.BorderLayout
import java.awt.Color
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
    private var isInitialized = false
    private var isInitializing = false
    var currentHtmlContent: String = ""
        private set
    var currentWindowKey: String = ""
        private set
    
    fun ensureInitialized() {
        if (isInitialized) {
            Napier.d("✅ SharedWindowManager already initialized")
            return
        }
        
        Napier.d("🔧 Initializing shared JavaFX WebView window")
        createWindow()
        isInitialized = true
    }
    
    fun updateTheme(isDarkTheme: Boolean) {
        if (currentHtmlContent.isEmpty() || webView == null) {
            Napier.d("⚠️ Cannot update theme: content or webView is null")
            return
        }
        
        Napier.d("🎨 Updating theme to: ${if (isDarkTheme) "dark" else "light"}")
        
        val themedHtml = injectThemeCSS(currentHtmlContent, isDarkTheme)
        
        Platform.runLater {
            try {
                webView?.engine?.loadContent(themedHtml, "text/html")
                Napier.d("✅ Theme updated successfully")
            } catch (e: Exception) {
                Napier.e("❌ Error updating theme", e)
            }
        }
        
        SwingUtilities.invokeLater {
            window?.contentPane?.background = if (isDarkTheme) {
                Color(0x12, 0x12, 0x12)
            } else {
                Color.WHITE
            }
        }
    }
    
    fun setCurrentContent(htmlContent: String, windowKey: String) {
        currentHtmlContent = htmlContent
        currentWindowKey = windowKey
        Napier.d("📝 Set current content for window: $windowKey")
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
 * Inject theme-aware CSS into HTML content
 */
private fun injectThemeCSS(htmlContent: String, isDarkTheme: Boolean): String {
    val themeCSS = if (isDarkTheme) {
        """
        <style id="theme-override">
            /* Dark Theme Override */
            :root {
                color-scheme: dark;
            }
            
            /* Base colors and font */
            html, body {
                background-color: #121212 !important;
                color: #e0e0e0 !important;
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif !important;
            }
            
            /* Force sans-serif font on all elements */
            * {
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif !important;
            }
            
            /* Containers and cards - Force dark backgrounds */
            .app-container, .screen, .component, .component-card, 
            [class*="container"], [class*="card"], [class*="panel"],
            div[style*="background"], div[style*="background-color"] {
                background-color: #1e1e1e !important;
                color: #e0e0e0 !important;
                border-color: #424242 !important;
            }
            
            /* Force dark backgrounds on any light colored elements */
            *[style*="background-color: rgb(255"], 
            *[style*="background-color: #fff"],
            *[style*="background-color: white"],
            *[style*="background: rgb(255"],
            *[style*="background: #fff"],
            *[style*="background: white"] {
                background-color: #1e1e1e !important;
                background: #1e1e1e !important;
                color: #e0e0e0 !important;
            }
            
            /* Headers */
            .app-header, .screen-title, [class*="header"] {
                background-color: #1e1e1e !important;
                color: #e0e0e0 !important;
                border-color: #424242 !important;
            }
            
            /* Fix gradient titles - JavaFX doesn't support gradients well */
            .app-title, [class*="title"] {
                background: none !important;
                -webkit-background-clip: unset !important;
                -webkit-text-fill-color: unset !important;
                background-clip: unset !important;
                color: #bb86fc !important;
            }
            
            /* Text elements - Force readable colors */
            h1, h2, h3, h4, h5, h6, p, span, label, li, td, th, a, div, strong, em, i, b {
                color: #e0e0e0 !important;
            }
            
            /* Override any light text on light backgrounds */
            * {
                color: #e0e0e0 !important;
            }
            
            /* Specific overrides for elements that should keep their color */
            button, [role="button"], .button, .btn {
                color: #000000 !important;
            }
            
            /* Inputs and form elements */
            input, textarea, select {
                background-color: #2c2c2c !important;
                color: #e0e0e0 !important;
                border-color: #424242 !important;
            }
            
            input::placeholder, textarea::placeholder {
                color: #888888 !important;
            }
            
            /* Buttons */
            button, [role="button"], .button, .btn {
                background-color: #bb86fc !important;
                color: #000000 !important;
                border-color: #bb86fc !important;
            }
            
            button:hover, [role="button"]:hover, .button:hover, .btn:hover {
                background-color: #9965f4 !important;
            }
            
            /* Tables - Force dark backgrounds */
            table {
                background-color: #1e1e1e !important;
                color: #e0e0e0 !important;
            }
            
            thead, tbody, tfoot {
                background-color: #1e1e1e !important;
            }
            
            th, td {
                background-color: #2c2c2c !important;
                color: #e0e0e0 !important;
                border-color: #424242 !important;
            }
            
            /* Table headers specifically */
            th {
                background-color: #333333 !important;
                color: #e0e0e0 !important;
                font-weight: bold !important;
            }
            
            /* Scrollbars */
            ::-webkit-scrollbar {
                width: 12px;
                height: 12px;
            }
            
            ::-webkit-scrollbar-track {
                background: #121212;
            }
            
            ::-webkit-scrollbar-thumb {
                background: #424242;
                border-radius: 6px;
            }
            
            ::-webkit-scrollbar-thumb:hover {
                background: #555555;
            }
            
            * {
                scrollbar-color: #424242 #121212;
                scrollbar-width: thin;
            }
        </style>
        """.trimIndent()
    } else {
        """
        <style id="theme-override">
            /* Light Theme - Minimal Override */
            :root {
                color-scheme: light;
            }
            
            html, body {
                background-color: #ffffff !important;
                color: #000000 !important;
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif !important;
            }
            
            /* Force sans-serif font on all elements */
            * {
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif !important;
            }
            
            /* Fix gradient titles - JavaFX doesn't support gradients well */
            .app-title, [class*="title"] {
                background: none !important;
                -webkit-background-clip: unset !important;
                -webkit-text-fill-color: unset !important;
                background-clip: unset !important;
                color: #667eea !important;
            }
        </style>
        """.trimIndent()
    }
    
    // Inject CSS right after <head> tag or at the beginning if no head tag
    return if (htmlContent.contains("<head>", ignoreCase = true)) {
        htmlContent.replaceFirst(
            Regex("<head>", RegexOption.IGNORE_CASE),
            "<head>\n$themeCSS"
        )
    } else if (htmlContent.contains("<html>", ignoreCase = true)) {
        htmlContent.replaceFirst(
            Regex("<html>", RegexOption.IGNORE_CASE),
            "<html>\n<head>\n$themeCSS\n</head>"
        )
    } else {
        "<html><head>$themeCSS</head><body>$htmlContent</body></html>"
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
    val appSettings = LocalAppSettings.current
    // Read the theme directly - this will cause recomposition when it changes
    val isDarkTheme = appSettings.isDarkTheme
    val windowKey = key.toString()
    
    Napier.d("🌐 HtmlViewer rendering HTML (length: ${htmlContent.length}) for key: $windowKey, darkTheme: $isDarkTheme")
    
    // Register theme change callback ONCE
    LaunchedEffect(Unit) {
        Napier.d("🔧 Registering theme change callback")
        appSettings.onThemeChanged = { newTheme ->
            Napier.d("🎨 Theme callback invoked: ${if (newTheme) "dark" else "light"}")
            SharedWindowManager.updateTheme(newTheme)
        }
    }
    
    // Effect for loading HTML content
    LaunchedEffect(key, htmlContent) {
        Napier.d("LaunchedEffect triggered for key: $windowKey")
        
        // Save content to SharedWindowManager
        SharedWindowManager.setCurrentContent(htmlContent, windowKey)
        
        // Ensure window is initialized
        SharedWindowManager.ensureInitialized()
        
        // Wait for window to be ready
        var retries = 0
        while (SharedWindowManager.webView == null && retries < 50) {
            kotlinx.coroutines.delay(100)
            retries++
        }
        
        if (SharedWindowManager.webView != null) {
            // Inject theme CSS into HTML content
            val themedHtmlContent = injectThemeCSS(htmlContent, isDarkTheme)
            
            // Update content
            Platform.runLater {
                try {
                    Napier.d("Loading HTML content for: $windowKey with theme: ${if (isDarkTheme) "dark" else "light"}")
                    SharedWindowManager.webView!!.engine.loadContent(themedHtmlContent, "text/html")
                } catch (e: Exception) {
                    Napier.e("Error loading HTML", e)
                }
            }
            
            // Update window background color based on theme
            SwingUtilities.invokeLater {
                SharedWindowManager.window?.apply {
                    title = "Prototipo: $windowKey"
                    contentPane.background = if (isDarkTheme) {
                        Color(0x12, 0x12, 0x12) // #121212
                    } else {
                        Color.white
                    }
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
