package app.prototype.creator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// Public function to update theme dynamically
fun updateWebViewTheme(isDarkTheme: Boolean) {
    SharedWindowManager.updateTheme(isDarkTheme)
}

// Singleton to manage shared window - JavaFX WebView can't be reliably recreated
private object SharedWindowManager {
    var window: JFrame? = null
    var webView: WebView? = null
    private var isInitializing = false
    private val initLock = Any()
    private var currentTheme: Boolean = false // false = light, true = dark
    private var originalHtml: String = "" // Store original HTML for theme switching
    
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
    
    fun setOriginalHtml(html: String) {
        originalHtml = html
    }
    
    fun updateTheme(isDarkTheme: Boolean) {
        if (currentTheme == isDarkTheme) return // No change
        if (originalHtml.isEmpty()) {
            Napier.w("⚠️ No original HTML stored, cannot update theme")
            return
        }
        
        currentTheme = isDarkTheme
        Napier.d("🎨 Updating WebView theme to: ${if (isDarkTheme) "DARK" else "LIGHT"}")
        
        Platform.runLater {
            if (isDarkTheme) {
                // Apply dark theme via JavaScript
                webView?.engine?.executeScript(
                    """
                    (function() {
                        console.log('🎨 Applying DARK theme via JavaScript...');
                        document.body.style.cssText = 'background: #121212 !important; color: #e0e0e0 !important;';
                        document.documentElement.style.cssText = 'background: #121212 !important; color: #e0e0e0 !important;';
                        
                        // Fix app-title specifically first
                        const appTitle = document.querySelector('.app-title');
                        if (appTitle) {
                            console.log('Found .app-title, fixing it...');
                            appTitle.style.background = 'none';
                            appTitle.style.webkitBackgroundClip = 'unset';
                            appTitle.style.webkitTextFillColor = '#bb86fc';
                            appTitle.style.backgroundClip = 'unset';
                            appTitle.style.color = '#bb86fc';
                        }
                        
                        const allElements = document.querySelectorAll('*');
                        allElements.forEach(function(el) {
                            const tagName = el.tagName.toLowerCase();
                            if (tagName === 'script' || tagName === 'style') return;
                            
                            if (tagName === 'body' || tagName === 'html' || tagName === 'div' || tagName === 'section' || tagName === 'main' || tagName === 'article') {
                                el.style.backgroundColor = '#121212';
                                el.style.color = '#e0e0e0';
                            }
                            
                            const className = el.className;
                            if (className && className.includes && className.includes('app-header')) {
                                el.style.backgroundColor = '#1e1e1e';
                                el.style.borderColor = '#333333';
                            }
                            if (className && className.includes && className.includes('app-title')) {
                                // Override gradient with solid color for dark theme
                                el.style.background = 'none !important';
                                el.style.webkitBackgroundClip = 'unset !important';
                                el.style.webkitTextFillColor = '#bb86fc !important';
                                el.style.backgroundClip = 'unset !important';
                                el.style.color = '#bb86fc !important';
                                console.log('Applied styles to app-title');
                            }
                            if (className && className.includes && className.includes('screen')) {
                                el.style.backgroundColor = '#1e1e1e';
                                el.style.borderColor = '#333333';
                                el.style.color = '#e0e0e0';
                            }
                            if (className && className.includes && className.includes('component')) {
                                el.style.backgroundColor = '#2a2a2a';
                                el.style.color = '#e0e0e0';
                            }
                            
                            if (tagName === 'input' || tagName === 'textarea' || tagName === 'select') {
                                el.style.backgroundColor = '#2a2a2a';
                                el.style.color = '#e0e0e0';
                                el.style.borderColor = '#444444';
                            }
                            if (tagName === 'button') {
                                el.style.background = 'linear-gradient(135deg, #bb86fc 0%, #9d6fd8 100%)';
                                el.style.color = '#ffffff';
                            }
                            if (tagName === 'h1') {
                                // Force h1 to be visible
                                el.style.background = 'none';
                                el.style.webkitBackgroundClip = 'unset';
                                el.style.webkitTextFillColor = '#bb86fc';
                                el.style.backgroundClip = 'unset';
                                el.style.color = '#bb86fc';
                            }
                            if (tagName === 'h2' || tagName === 'h3' || tagName === 'h4' || tagName === 'h5' || tagName === 'h6') {
                                el.style.color = '#bb86fc';
                            }
                        });
                        console.log('✅ Dark theme applied');
                    })();
                    """.trimIndent()
                )
            } else {
                // Apply light theme via JavaScript (don't reload to preserve state)
                Napier.d("☀️ Applying light theme via JavaScript")
                webView?.engine?.executeScript(
                    """
                    (function() {
                        console.log('☀️ Applying LIGHT theme via JavaScript...');
                        document.body.style.cssText = '';
                        document.documentElement.style.cssText = '';
                        
                        // Restore app-title with solid color (gradient doesn't work well)
                        const appTitle = document.querySelector('.app-title');
                        if (appTitle) {
                            console.log('Restoring .app-title...');
                            appTitle.style.removeProperty('background');
                            appTitle.style.removeProperty('-webkit-background-clip');
                            appTitle.style.removeProperty('-webkit-text-fill-color');
                            appTitle.style.removeProperty('background-clip');
                            appTitle.style.setProperty('color', '#667eea', 'important');
                        }
                        
                        const allElements = document.querySelectorAll('*');
                        allElements.forEach(function(el) {
                            const tagName = el.tagName.toLowerCase();
                            if (tagName === 'script' || tagName === 'style') return;
                            
                            // Reset backgrounds and colors to original
                            if (tagName === 'body' || tagName === 'html') {
                                el.style.backgroundColor = '#f8f9fa';
                                el.style.color = '#333333';
                            }
                            if (tagName === 'div' || tagName === 'section' || tagName === 'main' || tagName === 'article') {
                                el.style.backgroundColor = '';
                                el.style.color = '';
                            }
                            
                            const className = el.className;
                            if (className && className.includes && className.includes('app-header')) {
                                el.style.backgroundColor = '#ffffff';
                                el.style.borderColor = '#dee2e6';
                            }
                            if (className && className.includes && className.includes('screen')) {
                                el.style.backgroundColor = '#ffffff';
                                el.style.borderColor = '#dee2e6';
                                el.style.color = '';
                            }
                            if (className && className.includes && className.includes('component')) {
                                el.style.backgroundColor = '#f8f9fa';
                                el.style.color = '';
                            }
                            
                            if (tagName === 'input' || tagName === 'textarea' || tagName === 'select') {
                                el.style.backgroundColor = '#ffffff';
                                el.style.color = '#333333';
                                el.style.borderColor = '#dee2e6';
                            }
                            if (tagName === 'button') {
                                el.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
                                el.style.color = 'white';
                            }
                            if (tagName === 'h1') {
                                // Restore h1 with solid color
                                if (el.classList.contains('app-title')) {
                                    el.style.removeProperty('background');
                                    el.style.removeProperty('-webkit-background-clip');
                                    el.style.removeProperty('-webkit-text-fill-color');
                                    el.style.removeProperty('background-clip');
                                    el.style.setProperty('color', '#667eea', 'important');
                                } else {
                                    el.style.color = '';
                                }
                            }
                            if (tagName === 'h2' || tagName === 'h3' || tagName === 'h4' || tagName === 'h5' || tagName === 'h6') {
                                el.style.color = '#667eea';
                            }
                        });
                        console.log('✅ Light theme applied');
                    })();
                    """.trimIndent()
                )
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
    val appSettings = LocalAppSettings.current
    val isDarkTheme = appSettings.isDarkTheme
    
    Napier.d("🌐 HtmlViewer rendering HTML (length: ${htmlContent.length}) for key: $windowKey, darkTheme: $isDarkTheme")
    
    // Force reload when theme changes
    LaunchedEffect(key, htmlContent, isDarkTheme) {
=======
        Napier.d("🪟 LaunchedEffect triggered for key: $windowKey, isDarkTheme: $isDarkTheme")
    
        // Ensure window is initialized
        SharedWindowManager.ensureInitialized()
        
        // Wait for window to be ready
        var retries = 0
        while (SharedWindowManager.webView == null && retries < 50) {
            kotlinx.coroutines.delay(100)
            retries++
        }
        
        if (SharedWindowManager.webView != null) {
            // Store original HTML for theme switching
            SharedWindowManager.setOriginalHtml(htmlContent)
            
            // Inject theme-aware CSS
            val themedHtml = injectThemeStyles(htmlContent, isDarkTheme)
            
            // Debug: Log first 500 chars of themed HTML
            Napier.d("🎨 Themed HTML preview: ${themedHtml.take(500)}")
            Napier.d("🔧 Theme injection: isDark=$isDarkTheme, originalLength=${htmlContent.length}, newLength=${themedHtml.length}")
            
            // Update content on JavaFX thread
            Platform.runLater {
                try {
                    Napier.d("📝 Loading HTML content for: $windowKey with theme: ${if (isDarkTheme) "dark" else "light"}")
                    
                    // Force complete reload
                    SharedWindowManager.webView!!.engine.loadContent("", "text/html")
                    Thread.sleep(50)
                    SharedWindowManager.webView!!.engine.loadContent(themedHtml, "text/html")
                    
                    Napier.d("✅ HTML loaded successfully with theme: ${if (isDarkTheme) "DARK" else "LIGHT"}")
                } catch (e: Exception) {
                    Napier.e("❌ Error loading HTML", e)
                    e.printStackTrace()
                }
            }
            
            // Show and focus window
            SwingUtilities.invokeLater {
                SharedWindowManager.window?.apply {
                    title = "Prototipo: $windowKey ${if (isDarkTheme) "(Dark)" else "(Light)"}"
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

/**
 * Injects theme-specific CSS into the HTML content
 */
fun injectThemeStyles(htmlContent: String, isDarkTheme: Boolean): String {
    val themeStyles = if (isDarkTheme) {
        """
        <style id="theme-override">
            /* DARK THEME - AGGRESSIVE OVERRIDES */
            *, *::before, *::after {
                color: #e0e0e0 !important;
            }
            
            body, html { 
                background: #121212 !important; 
                background-color: #121212 !important;
                color: #e0e0e0 !important; 
            }
            
            div, section, article, main, aside, header, footer, nav {
                background: #121212 !important;
                color: #e0e0e0 !important;
            }
            
            .app-container, .app-container * { 
                background: #121212 !important; 
            }
            
            .app-header, .app-header * { 
                background: #1e1e1e !important; 
                background-color: #1e1e1e !important;
                border-color: #333333 !important; 
                color: #e0e0e0 !important;
            }
            
            .app-title {
                background: none !important;
                -webkit-background-clip: unset !important;
                -webkit-text-fill-color: #bb86fc !important;
                background-clip: unset !important;
                color: #bb86fc !important;
            }
            
            .app-description {
                color: #b0b0b0 !important;
            }
            
            .screen, .screen * { 
                background: #1e1e1e !important; 
                background-color: #1e1e1e !important;
                border-color: #333333 !important; 
                color: #e0e0e0 !important;
            }
            
            .screen-title, .screen-title * {
                color: #bb86fc !important; 
                border-bottom-color: #444444 !important; 
            }
            
            .component, .component * { 
                background: #2a2a2a !important; 
                background-color: #2a2a2a !important;
                border-left-color: #bb86fc !important; 
                color: #e0e0e0 !important;
            }
            
            .component-label, .component-label * { 
                color: #bb86fc !important; 
            }
            
            input, textarea, select, 
            input *, textarea *, select * { 
                background: #2a2a2a !important; 
                background-color: #2a2a2a !important;
                color: #e0e0e0 !important; 
                border-color: #444444 !important; 
            }
            
            input::placeholder, textarea::placeholder {
                color: #888888 !important;
            }
            
            input:focus, textarea:focus, select:focus { 
                border-color: #bb86fc !important; 
                box-shadow: 0 0 0 3px rgba(187, 134, 252, 0.2) !important; 
            }
            
            button, button * {
                background: linear-gradient(135deg, #bb86fc 0%, #9d6fd8 100%) !important;
                color: #ffffff !important;
                border: none !important;
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
            
            .nav-buttons button, .nav-buttons button * {
                background: linear-gradient(135deg, #bb86fc 0%, #9d6fd8 100%) !important;
                color: #ffffff !important;
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
            
            .component-card, .component-card * { 
                background: #2a2a2a !important; 
                background-color: #2a2a2a !important;
                border-color: #444444 !important; 
                color: #e0e0e0 !important;
            }
            
            .component-card h4, .component-card h3, .component-card h2 {
                color: #bb86fc !important;
            }
            
            .component-table, .component-table * {
                background: #2a2a2a !important;
                background-color: #2a2a2a !important;
                color: #e0e0e0 !important;
                border-color: #444444 !important;
            }
            
            .component-table th, .component-table th * { 
                background: #1e1e1e !important; 
                background-color: #1e1e1e !important;
                color: #bb86fc !important;
            }
            
            .component-table td {
                border-bottom-color: #444444 !important;
            }
            
            .component-list, .component-list * {
                color: #e0e0e0 !important;
            }
            
            .component-list li { 
                border-bottom-color: #444444 !important; 
            }
            
            .component-badge, .component-badge * {
                background: #bb86fc !important;
                background-color: #bb86fc !important;
                color: #ffffff !important;
            }
            
            .component-link, .component-link * {
                color: #bb86fc !important;
            }
            
            p, span, label, a, li, td, th, h1, h2, h3, h4, h5, h6 {
                color: #e0e0e0 !important;
            }
            
            h1, h2, h3, h4, h5, h6 {
                color: #bb86fc !important;
            }
        </style>
        <script>
            // ULTRA AGGRESSIVE theme application
            function applyDarkTheme() {
                console.log('🎨 Applying DARK theme forcefully via JavaScript...');
                
                // Force body styles
                document.body.style.cssText = 'background: #121212 !important; color: #e0e0e0 !important;';
                document.documentElement.style.cssText = 'background: #121212 !important; color: #e0e0e0 !important;';
                
                // Get ALL elements
                const allElements = document.querySelectorAll('*');
                
                allElements.forEach(function(el) {
                    const tagName = el.tagName.toLowerCase();
                    const className = el.className;
                    
                    // Skip script and style tags
                    if (tagName === 'script' || tagName === 'style') return;
                    
                    // Apply base dark styles to all elements
                    if (tagName === 'body' || tagName === 'html' || tagName === 'div' || 
                        tagName === 'section' || tagName === 'main' || tagName === 'article') {
                        el.style.backgroundColor = '#121212';
                        el.style.color = '#e0e0e0';
                    }
                    
                    // Headers
                    if (className && className.includes('app-header')) {
                        el.style.backgroundColor = '#1e1e1e';
                        el.style.borderColor = '#333333';
                    }
                    
                    // Screens
                    if (className && className.includes('screen')) {
                        el.style.backgroundColor = '#1e1e1e';
                        el.style.borderColor = '#333333';
                        el.style.color = '#e0e0e0';
                    }
                    
                    // Components
                    if (className && className.includes('component')) {
                        el.style.backgroundColor = '#2a2a2a';
                        el.style.color = '#e0e0e0';
                    }
                    
                    // Inputs
                    if (tagName === 'input' || tagName === 'textarea' || tagName === 'select') {
                        el.style.backgroundColor = '#2a2a2a';
                        el.style.color = '#e0e0e0';
                        el.style.borderColor = '#444444';
                    }
                    
                    // Buttons
                    if (tagName === 'button') {
                        el.style.background = 'linear-gradient(135deg, #bb86fc 0%, #9d6fd8 100%)';
                        el.style.color = '#ffffff';
                    }
                    
                    // Tables
                    if (tagName === 'table' || tagName === 'td' || tagName === 'tr') {
                        el.style.backgroundColor = '#2a2a2a';
                        el.style.color = '#e0e0e0';
                        el.style.borderColor = '#444444';
                    }
                    
                    if (tagName === 'th') {
                        el.style.backgroundColor = '#1e1e1e';
                        el.style.color = '#bb86fc';
                    }
                    
                    // Text elements
                    if (tagName === 'p' || tagName === 'span' || tagName === 'label' || 
                        tagName === 'a' || tagName === 'li') {
                        el.style.color = '#e0e0e0';
                    }
                    
                    // Headings
                    if (tagName === 'h1' || tagName === 'h2' || tagName === 'h3' || 
                        tagName === 'h4' || tagName === 'h5' || tagName === 'h6') {
                        el.style.color = '#bb86fc';
                    }
                });
                
                console.log('✅ Dark theme applied to ' + allElements.length + ' elements');
            }
            
            // Apply immediately when DOM is ready
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', applyDarkTheme);
            } else {
                applyDarkTheme();
            }
            
            // Also apply after a short delay to catch any dynamic content
            setTimeout(applyDarkTheme, 100);
            setTimeout(applyDarkTheme, 500);
        </script>
        """.trimIndent()
    } else {
        """
        <style id="theme-override">
            /* Light theme - fix title gradient issue */
            .app-title {
                background: none !important;
                -webkit-background-clip: unset !important;
                -webkit-text-fill-color: #667eea !important;
                background-clip: unset !important;
                color: #667eea !important;
            }
            
            /* Fix button text decoration */
            button, button * {
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
            
            .nav-buttons button, .nav-buttons button * {
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
        </style>
        """.trimIndent()
    }
    
    // Insert theme styles before </head> tag
    val result = when {
        htmlContent.contains("</head>", ignoreCase = true) -> {
            // Replace first occurrence of </head>
            val headIndex = htmlContent.indexOf("</head>", ignoreCase = true)
            htmlContent.substring(0, headIndex) + themeStyles + "\n" + htmlContent.substring(headIndex)
        }
        htmlContent.contains("<head>", ignoreCase = true) -> {
            // If there's a <head> but no </head>, insert after <head>
            val headIndex = htmlContent.indexOf("<head>", ignoreCase = true) + 6
            htmlContent.substring(0, headIndex) + "\n" + themeStyles + htmlContent.substring(headIndex)
        }
        htmlContent.contains("<html", ignoreCase = true) -> {
            // If there's <html> but no <head>, insert after <html>
            val htmlIndex = htmlContent.indexOf(">", htmlContent.indexOf("<html", ignoreCase = true)) + 1
            htmlContent.substring(0, htmlIndex) + "\n<head>$themeStyles</head>\n" + htmlContent.substring(htmlIndex)
        }
        else -> {
            // Last resort: prepend to entire content
            "<html><head>$themeStyles</head><body>" + htmlContent + "</body></html>"
        }
    }
    
    Napier.d("🔧 Theme injection: isDark=$isDarkTheme, originalLength=${htmlContent.length}, newLength=${result.length}")
    return result
}
